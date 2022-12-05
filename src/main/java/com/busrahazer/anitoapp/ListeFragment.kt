package com.busrahazer.anitoapp

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.database.getStringOrNull
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.Adapter
import kotlinx.android.synthetic.main.fragment_liste.*


class ListeFragment : Fragment() {

    //recyclerview içinde göstereceğimiz için bi dizi kullanacağız
    var baslikListesi =ArrayList<String>()
    var aniIdListesi = ArrayList<Int>()
    private lateinit var  listeAdapter: ListeRecyclerAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_liste, container, false)


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listeAdapter = ListeRecyclerAdapter(baslikListesi,aniIdListesi)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = listeAdapter //adaptöre erişildi
    sqlVeriAlma()

    }
    fun sqlVeriAlma(){
        try {
            //nullability sorunumuz var o yüzden ya context ya da activity'den çağırmamız gerekir. burda aktiviteden yaptık.
            activity?.let {
                val database = it.openOrCreateDatabase("Anilarimiz", Context.MODE_PRIVATE,null)

                val cursor = database.rawQuery("SELECT * FROM anilar",null)
                val baslikIndex = cursor.getColumnIndex("baslik")
                val aniIdIndex = cursor.getColumnIndex("id")

                baslikListesi.clear()
                aniIdListesi.clear()

                while(cursor.moveToNext()){

                    //burada eklenen elemanlarımızı diziye ekliyoruz. listeye görünecekler.
                    baslikListesi.add(cursor.getString(baslikIndex))
                    aniIdListesi.add(cursor.getInt(aniIdIndex))
                }
                listeAdapter.notifyDataSetChanged() //veriler değişince yani yeni veri gelince recyclerview'a ekleyecek

                cursor.close()
            }

        } catch (e: Exception){
            e.printStackTrace()
        }
    }




}