package com.busrahazer.anitoapp

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import kotlinx.android.synthetic.main.fragment_ani.*
import java.io.ByteArrayOutputStream


class AniFragment : Fragment() {

    var secilenGorsel : Uri? = null
    var secilenBitmap : Bitmap? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ani, container, false)
    }

    //görünümlerle ilgili işlemleri buraya yapıyoruz
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //fragmentlarda butonlarla ya da görsel eklemelerle işlem yapacaksak fonksiyonları burada bir çağırmalıyız
        button.setOnClickListener{
            kaydet(it)
        }
        imageView.setOnClickListener{
            gorselSec(it)
        }

        arguments?.let {

            var gelenBilgi = AniFragmentArgs.fromBundle(it).bilgi

            if (gelenBilgi.equals("menudengeldim")){
                //yeni bir anı eklemeye geldi
                baslikText.setText("")
                notText.setText("")
                button.visibility = View.VISIBLE

                val gorselSecmeArkaPlani = BitmapFactory.decodeResource(context?.resources,R.drawable.cfoto)
                imageView.setImageBitmap(gorselSecmeArkaPlani)

            } else {
                //daha önce oluşturulan bir anıyı görmeye geldi
                button.visibility = View.INVISIBLE
                val secilenId = AniFragmentArgs.fromBundle(it).id

                context?.let {

                    try {

                        val db = it.openOrCreateDatabase("Anilarimiz",Context.MODE_PRIVATE,null)
                        val cursor = db.rawQuery("SELECT * FROM anilar WHERE id = ?", arrayOf(secilenId.toString()))

                        val baslikIndex = cursor.getColumnIndex("baslik")
                        val aniIndex = cursor.getColumnIndex("ani")
                        val gorsel = cursor.getColumnIndex("gorsel")

                        while(cursor.moveToNext()){
                            baslikText.setText(cursor.getString(baslikIndex))
                            notText.setText(cursor.getString(aniIndex))

                            val byteDizisi = cursor.getBlob(gorsel)
                            val bitmap = BitmapFactory.decodeByteArray(byteDizisi,0,byteDizisi.size)
                            imageView.setImageBitmap(bitmap)
                        }

                        cursor.close()

                    } catch (e: Exception){
                        e.printStackTrace()
                    }

                }

            }

        }

    }

    fun kaydet(view: View){
        val baslik = baslikText.text.toString()
        val ani = notText.text.toString()

        if (secilenBitmap != null) {

            val kucukBitmap = kucukBitmapOlustur(secilenBitmap!!,300)

            val outputStream = ByteArrayOutputStream() //görselimizi veriye ceviriyoruz yani veri dizisine. bu sınıf o işi görüyor.
            kucukBitmap.compress(Bitmap.CompressFormat.PNG,50,outputStream)
            val byteDizisi = outputStream.toByteArray()

            try{
                context?.let {
                    val database = it.openOrCreateDatabase("Anilarimiz", Context.MODE_PRIVATE,null)
                    database.execSQL("CREATE TABLE IF NOT EXISTS anilar (id INTEGER PRIMARY KEY, baslik VARCHAR(200), ani VARCHAR(240), gorsel BLOB)")
                    //database'in satırları oluştu

                    val sqlString = "INSERT INTO anilar ( baslik, ani, gorsel) VALUES (?, ?, ?)" //values sabit değiller o yüzden birbirine bağlamaya çalışıyoruz
                    val statement = database.compileStatement(sqlString) //string dizisine çevirerek birbirine bağlıyoruz
                    statement.bindString(1,baslik)
                    statement.bindString(2,ani)
                    statement.bindBlob(3,byteDizisi)
                    statement.execute()

                }

            } catch (e: Exception){
                e.printStackTrace()
            }
            //kayıt işleminden sonra liste fragmanına geri dönelim
            val action = AniFragmentDirections.actionAniFragmentToListeFragment()
            Navigation.findNavController(view).navigate(action)

        }


    }


    fun gorselSec(view: View){

        activity?.let {
                //uyumsuzlugu gidermek için.izin kontrolü için.(aktivite.istenilen context , hangi izni istediği) != izin verildi
            if(ContextCompat.checkSelfPermission(it.applicationContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            //izin verilmedi, sormak gerekiyor.
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
                }
            else{
            //izin zaten verilmiş, tekrar sorma
                val galeriIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI) //galerinin konumunu anlattık
                startActivityForResult(galeriIntent,2) //burada bir result deger döndürmesi gerektiği için bu fonksiyonu kullandık
            }

        }


    }
    //istenilen iznin sonuçları fonksiyonu
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode == 1){

            if(grantResults.size > 0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                //izni aldık
             val galeriIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI) //galerinin konumunu anlattık
            startActivityForResult(galeriIntent,2)
        }

        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
    //galeriye gidilirse ne olacak
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        //izin kodum 2 mi bakıyorum && cevap kodum dogru mu yani görsele tıkladık mı ve geriye bir data döndü mü
        if(requestCode == 2 && resultCode == Activity.RESULT_OK && data != null){
            secilenGorsel = data.data //seçilen görselin telefonda nerede durduğunu aldık.

            try {

                context?.let {
                    if (secilenGorsel != null) { //gorsel null olmasın ki bulabilelim ve kullanalım
                        //android10 ve üzerinde geçerli olan
                        if (Build.VERSION.SDK_INT >= 28) {
                            val source = ImageDecoder.createSource(it.contentResolver, secilenGorsel!!)
                            secilenBitmap = ImageDecoder.decodeBitmap(source)
                            imageView.setImageBitmap(secilenBitmap)
                        } else {
                            secilenBitmap =
                                MediaStore.Images.Media.getBitmap(it.contentResolver, secilenGorsel)
                            imageView.setImageBitmap(secilenBitmap)
                        }
                    }
                }
            }
            catch(e : Exception){
                e.printStackTrace()
            }
        }


        super.onActivityResult(requestCode, resultCode, data)
}
        //bitmap 1 mb' dan fazla saklayamıyor. eger görsel 1 mb' dan büyükse boyutunu değiştirmemiz gerekir.

        fun kucukBitmapOlustur(kullanicininSectigiBitmap: Bitmap, maximumBoyut: Int) : Bitmap {

            var width = kullanicininSectigiBitmap.width
            var height = kullanicininSectigiBitmap.height

            val bitmapOrani : Double = width.toDouble() / height.toDouble() //buradan görselin yatay mı dikey mi olduguna bakıyoruz

            if (bitmapOrani > 1) {
                // görselimiz yatay
                width = maximumBoyut
                val kisaltilmisHeight = width / bitmapOrani
                height = kisaltilmisHeight.toInt()
            } else {
                //görselimiz dikey
                height = maximumBoyut
                val kisaltilmisWidth = height * bitmapOrani
                width = kisaltilmisWidth.toInt()

            }


            return Bitmap.createScaledBitmap(kullanicininSectigiBitmap,width,height,true)
        }

    }
