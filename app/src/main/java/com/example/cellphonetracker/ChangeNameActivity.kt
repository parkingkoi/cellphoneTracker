package com.example.cellphonetracker

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cellphonetracker.Interfaces.ApiService
import com.example.cellphonetracker.adapter.DeviceAdapter
import com.example.cellphonetracker.serverCalls.NetworkClient
import kotlinx.android.synthetic.main.activity_change_name.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class ChangeNameActivity : AppCompatActivity() {
   

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_name)

//        change.setOnClickListener { object : View.OnClickListener{
//            override fun onClick(v: View?) {
//                Log.e("clock","click")
//                Toast.makeText(this@ChangeNameActivity,"Enter first and last name",Toast.LENGTH_SHORT).show()
//                val f_name = first_name.text
//                val l_name = last_name.text
//                if(!f_name.isEmpty() && !l_name.isEmpty()){
//                    changeName(f_name.toString()+" "+l_name.toString(),intent.getStringExtra("device"))
//                }else{
//                    Toast.makeText(this@ChangeNameActivity,"Enter first and last name",Toast.LENGTH_SHORT).show()
//                }
//
//            }
//
//        } }

    }


     fun changeName(name: String,device: String){
        val service: ApiService = NetworkClient.getRetrofitClient().create(ApiService::class.java)
        val call: Call<String>
        call = service.updateName(name,device)
        call.enqueue(object : Callback<String>{
            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.e("error",t.toString())

            }

            override fun onResponse(call: Call<String>, response: Response<String>) {
                finish()
            }

        })
    }

    fun click(view: View){
        Log.e("clock","click")
        val f_name = first_name.text
        val l_name = last_name.text
        if(!f_name.isEmpty() && !l_name.isEmpty()){
            changeName(f_name.toString()+" "+l_name.toString(),intent.getStringExtra("device"))
        }else{
            Toast.makeText(this@ChangeNameActivity,"Enter first and last name",Toast.LENGTH_SHORT).show()
        }

    }


}
