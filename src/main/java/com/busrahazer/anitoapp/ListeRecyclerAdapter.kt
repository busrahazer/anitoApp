package com.busrahazer.anitoapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.recycler_row.view.*

class ListeRecyclerAdapter(val aniListesi: ArrayList<String>, val idListesi : ArrayList<Int>) : RecyclerView.Adapter<ListeRecyclerAdapter.AniHolder>() {

    class AniHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }
    //bir aniHolder döndürmemi istiyor. tasarımı aslında.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AniHolder {
        val inflater = LayoutInflater.from(parent.context)
        //görünümümüzü oluşturuyoruz
        val view = inflater.inflate(R.layout.recycler_row,parent,false)
        return AniHolder(view)
    }
    //kaç tane recyclerview oluşturacağımızı söylüyor
    override fun getItemCount(): Int {
        return aniListesi.size
    }
    //recyclerview'da görünecek textlerin içinde girdiğimiz başlıklar görünüyor
    override fun onBindViewHolder(holder: AniHolder, position: Int) {
        holder.itemView.testText.text = aniListesi[position] // her veride bi sonraki boş pozisyona başlıklar ekleniyor
        holder.itemView.setOnClickListener { //recyclerview'a eklediğimiz bir başlığa tıkladığımızda ne olacağını yazıyoruz

            //aksiyonlar ile nereye gideceğimizi seçebiliyoruz - burada fragmanlar arası geçiş
           val action = ListeFragmentDirections.actionListeFragmentToAniFragment("recyclerdangeldim",idListesi[position])
            Navigation.findNavController(it).navigate(action)
        }

}}