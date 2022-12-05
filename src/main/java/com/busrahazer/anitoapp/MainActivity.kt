package com.busrahazer.anitoapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.navigation.Navigation


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    // menuyu bağlama işlemi
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.ani_ekle,menu)

        return super.onCreateOptionsMenu(menu)
    }

    //menuden item seçilirse
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        //birden fazla seçenek olursa diye kontrol etmek gerek
        if(item.itemId==R.id.ani_ekle_item){
            val action = ListeFragmentDirections.actionListeFragmentToAniFragment("menudengeldim",0)
            Navigation.findNavController(this,R.id.fragment).navigate(action)
        }

        return super.onOptionsItemSelected(item)
    }
}