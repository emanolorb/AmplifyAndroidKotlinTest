package com.example.amplifytest

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobile.client.Callback
import com.amazonaws.mobile.client.UserState
import com.amazonaws.mobile.client.UserStateDetails
import com.amazonaws.mobile.client.results.SignInResult
import com.amazonaws.mobile.client.results.SignInState
import com.amazonaws.mobile.client.results.SignUpResult
import com.amazonaws.mobile.client.results.UserCodeDeliveryDetails
import com.amazonaws.services.cognitoidentityprovider.model.UserNotConfirmedException
import com.amazonaws.services.cognitoidentityprovider.model.UsernameExistsException
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        AWSMobileClient.getInstance()
            .initialize(this, object : Callback<UserStateDetails?> {

                override fun onResult(userStateDetails: UserStateDetails?) {
                    when (userStateDetails?.userState) {
                        UserState.SIGNED_IN -> runOnUiThread {
                            val textView =
                                findViewById<View>(R.id.text1) as TextView
                            textView.text = "Logged IN"
                        }
                        UserState.SIGNED_OUT -> runOnUiThread {
                            val textView =
                                findViewById<View>(R.id.text1) as TextView
                            textView.text = "Logged OUT"
                        }
                        else -> AWSMobileClient.getInstance().signOut()
                    }
                }
                override fun onError(e: Exception?) {
                    Log.e("INIT", "Initialization error.", e)
                }
            })

        btGetLogin.setOnClickListener {
            SingIn(etUsername.text.toString(),etPass.text.toString(), this)
        }
        btSignUP.setOnClickListener {
            SignUp(this)
        }
        btConfirm.setOnClickListener {
            ConfirmSingUp(this)
        }
    }
    fun ConfirmSingUp(context: Context){
        makeToast("Este es el confirm", context)
        val username: String = etUsername.text.toString()
        val code: String = etCode.text.toString()
        AWSMobileClient.getInstance().confirmSignUp(
            username,
            code,
            object :
                Callback<SignUpResult> {
                override fun onResult(signUpResult: SignUpResult) {
                    runOnUiThread {
                        Log.d(
                            "signUpResult",
                            "Sign-up callback state: " + signUpResult.confirmationState
                        )
                        if (!signUpResult.confirmationState) {
                            val details =
                                signUpResult.userCodeDeliveryDetails
                            makeToast("Confirm sign-up with: " + details.destination, context)
                        } else {
                            makeToast("Sign-up done.", context)
                        }
                    }
                }

                override fun onError(e: java.lang.Exception) {
                    makeToast("errorConfirm", context)
                    Log.e("signUpResult", "Confirm sign-up error", e)
                }
            })
    }
    fun SignUp(context:Context){
        makeToast("SignUP", context)
        val username: String = etUsername.text.toString()
        val password: String = etPass.text.toString()
        val attributes: MutableMap<String, String> = HashMap()
        attributes["email"] = "emmanuel.ramirez@cherrypop.app"
        attributes["name"] = "Emmanuel Ramirez"
        AWSMobileClient.getInstance().signUp(
            username,
            password,
            attributes,
            null,
            object : Callback<SignUpResult> {
                override fun onResult(signUpResult: SignUpResult) {
                    runOnUiThread {
                        Log.d(
                            "SignUpResult",
                            "Sign-up callback state: " + signUpResult.getConfirmationState()
                        )
                        if (!signUpResult.getConfirmationState()) {
                            val details: UserCodeDeliveryDetails =
                                signUpResult.getUserCodeDeliveryDetails()
                            makeToast("Confirm sign-up with: " + details.destination, context)
                        } else {
                            makeToast("Sign-up done.", context)
                        }
                    }
                }

                override fun onError(e: java.lang.Exception) {
                    if (e is UsernameExistsException) {
                        println("Usuario ya existe compa")
                    } else {
                        println("No estoy cachando este error" + e.toString())
                    }
                    // Log.e("SignUpResult", "Sign-up error", e)
                }
            })
    }

    fun SingIn(username:String, password:String, context: Context){
        AWSMobileClient.getInstance().signIn(
            username,
            password,
            null,
            object : Callback<SignInResult> {
                override fun onResult(signInResult: SignInResult) {
                    runOnUiThread {
                        println("############################################################")
                        println(signInResult)
                        println("---------------------------------------------------------")
                        println(signInResult)
                        Log.d(
                            "SingInFuction",
                            "Sign-in callback state: " + signInResult.signInState
                        )
                        when (signInResult.signInState) {
                            SignInState.DONE -> makeToast("Sign-in done.", context)
                            SignInState.SMS_MFA -> makeToast("Please confirm sign-in with SMS.", context)
                            SignInState.NEW_PASSWORD_REQUIRED -> makeToast("Please confirm sign-in with new password.", context)
                            else -> makeToast("Unsupported sign-in confirmation: " + signInResult.signInState, context)
                        }
                    }
                }

                override fun onError(e: java.lang.Exception) {
                    if (e is UserNotConfirmedException) {
                        println("Usuario no confirmado a la verga compa")
                    } else {
                        println("No estoy cachando este error" + e.toString())
                    }
                }
            })
    }
    fun makeToast(msj:String, context: Context){
        Toast.makeText(context,msj,Toast.LENGTH_SHORT)
        println("############################################################")
        println(msj)
        println("############################################################")
    }
}
