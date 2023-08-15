package com.example.stepstracker

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat


class MainActivity : AppCompatActivity(), SensorEventListener {
    
    private var sensorManager: SensorManager? = null


    private var running = false


    private var totalSteps = 0f


    private var previousTotalSteps = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestPermissions()
        loadData()
        resetSteps()

    
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    override fun onResume() {
        super.onResume()
        running = true

        
        val stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)


        if (stepSensor == null) {
            Toast.makeText(this, "No sensor detected on this device", Toast.LENGTH_SHORT).show()
        } else {
            sensorManager?.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {


        val tvStepsTaken = findViewById<TextView>(R.id.tv_stepsTaken)

        if (running) {
            totalSteps = event!!.values[0]

            
            val currentSteps = totalSteps.toInt() - previousTotalSteps.toInt()

            
            tvStepsTaken.text = ("$currentSteps")
        }
    }

    private fun resetSteps() {
        val tvStepsTaken = findViewById<TextView>(R.id.tv_stepsTaken)
        val resetBtn:Button = findViewById(R.id.Resetbtn)
        resetBtn.setOnClickListener {
            previousTotalSteps = totalSteps

            
            tvStepsTaken.text = 0.toString()

            
            saveData()


        }

    }

    private fun saveData() {

       
        val sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)

        val editor = sharedPreferences.edit()
        editor.putFloat("key1", previousTotalSteps)
        editor.apply()
    }

    private fun loadData() {

       
        val sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val savedNumber = sharedPreferences.getFloat("key1", 0f)


        Log.d("MainActivity", "$savedNumber")

        previousTotalSteps = savedNumber
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        
    }

    private fun hasActivityRecognition() = ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED

    private fun requestPermissions(){
        val permissionToRequest = mutableListOf<String>()

        if(!hasActivityRecognition()){
            permissionToRequest.add(android.Manifest.permission.ACTIVITY_RECOGNITION)
        }

        if(permissionToRequest.isNotEmpty()){
            ActivityCompat
                .requestPermissions(this, permissionToRequest.toTypedArray(), 0 )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(grantResults.isNotEmpty()&&requestCode==0){
            for(i in grantResults.indices){
                if(grantResults[i]==PackageManager.PERMISSION_GRANTED){
                    Log.d("permission","${grantResults[i]} permission granted")
                }
            }
        }
    }
}
