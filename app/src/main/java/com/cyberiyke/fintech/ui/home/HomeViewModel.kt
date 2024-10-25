package com.cyberiyke.fintech.ui.home

import android.app.Application
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.navigation.Navigation
import com.cyberiyke.fintech.R
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.cyberiyke.fintech.ui.dialogs.ConfirmPinDialog
import com.cyberiyke.fintech.data.model.Statement
import com.cyberiyke.fintech.data.model.Users
import com.cyberiyke.fintech.ui.auth.SetNewPinActivity
import com.cyberiyke.fintech.ui.dialogs.ProgressDialog
import com.cyberiyke.fintech.utils.StatementClickInterface
import com.cyberiyke.fintech.utils.UserClickInterface
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.Query
import com.iyke.onlinebanking.utils.Constants
import com.iyke.onlinebanking.utils.Constants.AMOUNT
import com.iyke.onlinebanking.utils.Constants.BALANCE
import com.iyke.onlinebanking.utils.Constants.CLIENT_NAME
import com.iyke.onlinebanking.utils.Constants.MESSAGE
import com.iyke.onlinebanking.utils.Constants.STATEMENT
import com.iyke.onlinebanking.utils.Constants.TIME
import com.iyke.onlinebanking.utils.Constants.TYPE
import com.iyke.onlinebanking.utils.Constants.USERS
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.random.Random
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.iyke.onlinebanking.utils.Constants.PIN
import timber.log.Timber

class HomeViewModel(application: Application) : AndroidViewModel(application), UserClickInterface<Users>, StatementClickInterface<Statement> {


    private val context = getApplication<Application>().applicationContext
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        Constants.PREFERENCE, AppCompatActivity.MODE_PRIVATE
    )
     var basicListener: UserClickInterface<Users> = this
     var statementlistener: StatementClickInterface<Statement> = this
    private val firebaseEmail: String? = sharedPreferences.getString(Constants.EMAIL, "")
    private val displayName: String? = sharedPreferences.getString(Constants.NAME, "")

    val userData = MutableLiveData<Users>()
    var users = MutableLiveData<List<Users>>()
    val statements = MutableLiveData<List<Statement>>()
    val amountAdded = MutableLiveData<String>()
    val addMoney = MutableLiveData<String>()
    val message = MutableLiveData<String>()

    private lateinit var clickedUser: Users
    private lateinit var homeFragment: View
    private lateinit var sendFragment: View

    init {
        addMoney.value = ""
        amountAdded.value = ""
        message.value = ""
    }

    private fun addFunds(view: View) {
        val amount = addMoney.value?.toIntOrNull() ?: run {
            view.showToast("No amount added")
            return
        }

        val progressDialog = ProgressDialog(view.context).apply { show() }
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val userDocRef = db.collection(Constants.USERS).document(firebaseEmail!!)
                val userDoc = userDocRef.get().await()
                val currentBalance = userDoc[Constants.BALANCE].toString().toInt()

                // Update balance
                userDocRef.update(Constants.BALANCE, currentBalance + amount).await()

                // Add statement
                val statementData = hashMapOf(
                    Constants.AMOUNT to amount,
                    Constants.CLIENT_NAME to "my Bank",
                    Constants.TIME to Timestamp.now(),
                    Constants.TYPE to "Added"
                )
                val transactionId = generateTransactionId()
                db.collection(Constants.USERS).document(firebaseEmail)
                    .collection(Constants.STATEMENT).document(transactionId)
                    .set(statementData).await()

                view.showToast("$amount Added successfully")
                Navigation.findNavController(view).popBackStack()

            } catch (e: Exception) {
                view.showToast("Failed to add funds: ${e.message}")
            } finally {
                progressDialog.dismiss()
            }
        }
    }

    fun fetchUserDetails() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val userDoc = db.collection(Constants.USERS).document(firebaseEmail!!).get().await()
                userData.value = userDoc.toObject(Users::class.java)
            } catch (e: Exception) {
                logError("Fetch User", e)
            }
        }
    }

    fun navigateView(view: View) {
        val actionId = when (view.id) {
            R.id.addFunds -> R.id.action_navigation_home_to_addMoneyFragment
            R.id.sendMoney -> R.id.action_navigation_home_to_sendMoneyFragment
            R.id.see -> R.id.action_navigation_home_to_transactFragment
            R.id.confirmAddMoney -> {
                addFunds(view)
                return
            }
            R.id.confirmSend -> {
                verifyAmount(view)
                return
            }
            else -> null
        }
        actionId?.let { Navigation.findNavController(view).navigate(it) }
    }

    private fun verifyAmount(view: View) {
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection(USERS).document(firebaseEmail!!)

        docRef.get()
            .addOnSuccessListener { document ->
                val balance = document[BALANCE]?.toString()?.toIntOrNull()
                val pinExists = document[PIN] != null
                val amountToAdd = amountAdded.value?.toIntOrNull() ?: 0

                when {
                    balance == null -> {
                        Timber.tag("SendMoneyActivity").d("No such document")
                        Toast.makeText(context, "Documents not found", Toast.LENGTH_SHORT).show()
                    }
                    balance < amountToAdd -> {
                        Toast.makeText(context, "Insufficient funds", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        Toast.makeText(context, "Possible", Toast.LENGTH_SHORT).show()
                        if (pinExists) {
                            val callBox = ConfirmPinDialog(view.context)
                            callBox.show()
                            callBox.setOnDismissListener {
                                if (callBox.confirmed) {
                                    sendMoney(balance, view)
                                }
                            }
                        } else {
                            view.context.startActivity(Intent(context, SetNewPinActivity::class.java))
                        }
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.d("SendMoneyActivity", "Get failed with ", exception)
                Toast.makeText(context, "Balance checking failed", Toast.LENGTH_SHORT).show()
            }

    }

    private fun showConfirmPinDialog(balance:Int, view: View) {
        val dialog = ConfirmPinDialog(view.context).apply { show() }
        dialog.setOnDismissListener {
            if (dialog.confirmed) {
                sendMoney(balance,view)
            }
        }
    }

    fun fetchStatement(fragment: View) {
        // Use a modern progress indicator instead of ProgressDialog
        val progressBar = CircularProgressIndicator(fragment.context).apply {
            isIndeterminate = true
            visibility = View.VISIBLE
        }
        // Add the progress bar to the root layout
        (fragment as? ViewGroup)?.addView(progressBar)

        homeFragment = fragment
        val statementArray = mutableListOf<Statement>()

        db.collection(USERS).document(firebaseEmail.orEmpty())
            .collection(STATEMENT).orderBy("time", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                // Use map to transform documents into a list of Statements
                statementArray.addAll(documents.map { doc ->
                    Statement(
                        amount = doc[AMOUNT].toString(),
                        type = doc[TYPE].toString(),
                        client = doc[CLIENT_NAME].toString(),
                        time = doc[TIME] as Timestamp,
                        message = doc[MESSAGE].toString()
                    )
                })
                statements.value = statementArray
            }
            .addOnFailureListener { exception ->
                Log.e("FetchStatement", "Failed to fetch statements", exception)
                // Optionally, show a message to the user indicating an error occurred
                Toast.makeText(fragment.context, "Failed to load statements", Toast.LENGTH_SHORT).show()
            }
            .addOnCompleteListener {
                // Dismiss the progress bar after the task is complete
                progressBar.visibility = View.GONE
                (fragment as? ViewGroup)?.removeView(progressBar)
            }
    }

    private fun sendMoney(myBalance: Int, view: View) {
        // Use MaterialProgressIndicator for modern progress indication
        val progressBar = CircularProgressIndicator(view.context).apply {
            isIndeterminate = true
            visibility = View.VISIBLE
        }
        (view as? ViewGroup)?.addView(progressBar)

        val db = FirebaseFirestore.getInstance()
        val clickedUserEmail = clickedUser.email.orEmpty()
        val amountToSend = amountAdded.value?.toInt() ?: 0

        // Fetch the recipient's balance
        db.collection(USERS).document(clickedUserEmail).get()
            .addOnSuccessListener { document ->
                val recipientBalance = document[BALANCE]?.toString()?.toIntOrNull() ?: 0

                // Update recipient's balance
                val updateRecipientBalance = db.collection(USERS).document(clickedUserEmail)
                    .update(BALANCE, recipientBalance + amountToSend)

                // Update sender's balance
                val updateSenderBalance = db.collection(USERS).document(firebaseEmail!!)
                    .update(BALANCE, myBalance - amountToSend)

                // Create a transaction ID
                val txId = "TID-SM-" + Random.nextBytes(9).joinToString("")

                // Prepare statement data for sender and recipient
                val timestamp = Timestamp.now()
                val myStatementData = mapOf(
                    AMOUNT to -amountToSend,
                    CLIENT_NAME to "to ${clickedUser.name}",
                    TIME to timestamp,
                    MESSAGE to message.value.orEmpty(),
                    TYPE to "Debited"
                )
                val clientStatementData = mapOf(
                    AMOUNT to amountToSend,
                    CLIENT_NAME to "from $displayName",
                    TIME to timestamp,
                    MESSAGE to message.value.orEmpty(),
                    TYPE to "Credited"
                )

                // Update statements for both users
                val updateSenderStatement = db.collection(USERS)
                    .document(firebaseEmail!!).collection(STATEMENT).document(txId)
                    .set(myStatementData)

                val updateRecipientStatement = db.collection(USERS)
                    .document(clickedUserEmail).collection(STATEMENT).document(txId)
                    .set(clientStatementData)

                // Perform all updates in parallel
                Tasks.whenAll(
                    updateRecipientBalance, updateSenderBalance,
                    updateSenderStatement, updateRecipientStatement
                ).addOnCompleteListener { task ->
                    progressBar.visibility = View.GONE
                    (view as? ViewGroup)?.removeView(progressBar)

                    if (task.isSuccessful) {
                        Toast.makeText(context, "Transaction successful", Toast.LENGTH_SHORT).show()
                        Navigation.findNavController(view).popBackStack(R.id.navigation_home, false)
                    } else {
                        Toast.makeText(context, "Transaction failed, please try again.", Toast.LENGTH_SHORT).show()
                    }
                }.addOnFailureListener { exception ->
                    Log.e("SendMoney", "Transaction failed", exception)
                    Toast.makeText(context, "Transaction failed, please check your network.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Log.e("SendMoney", "Failed to fetch recipient's balance", exception)
                Toast.makeText(context, "Recipient not found, please try again.", Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.GONE
                (view as? ViewGroup)?.removeView(progressBar)
            }
    }

    private fun generateTransactionId() = "TID-SM-" + Random.nextBytes(9).joinToString("")

    private fun logError(tag: String, e: Exception) {
        Log.d(tag, "Error: ${e.message}", e)
    }

    private fun View.showToast(message: String) {
        Toast.makeText(this.context, message, Toast.LENGTH_SHORT).show()
    }


     override fun onUserClick(user: Users) {
         val bundle = Bundle()
         bundle.putParcelable("User", user)
         Navigation.findNavController(sendFragment)
             .navigate(R.id.action_sentFragment_to_sendMoneyFragment, bundle)
     }

     override fun onStatementClick(statement: Statement) {
         val bundle = Bundle()
         bundle.putParcelable("statement", statement)
         Navigation.findNavController(homeFragment)
             .navigate(R.id.action_navigation_home_to_transactFragment,bundle)
     }

     fun fetchUsers(view: View) {
         sendFragment = view
         val tempArr = ArrayList<Users>()
         db.collection(USERS).get().addOnSuccessListener { doc ->
             for (user in doc) {
                 val user = user.toObject(Users::class.java)
                 if (user.email != firebaseEmail){
                     tempArr.add(user)
                 }
             }
             users.value = tempArr

         }.addOnFailureListener { Log.d("VerifyActivity", "Log in failed because ${it.message}") }
     }
 }
