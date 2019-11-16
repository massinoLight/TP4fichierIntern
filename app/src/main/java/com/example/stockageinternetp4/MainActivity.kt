package com.example.stockageinternetp4

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import android.content.Intent
import android.os.Environment
import org.jetbrains.anko.startActivityForResult
import org.jetbrains.anko.toast
import java.io.File

/**
 * Dans la solution que nous proposons on choisis un fichier intern
 * car on éstime que les données traité (ici des contacts)ne devronts etre accéciblent
 * uniquement par notre application
 * et nous ne pouvont pas risquer de ne pas afficher les contacts en cas de non
 * disponibilité du stockage extern mais aussi nous n'aurons pas  besoin de l'autorisation de l'utilisateur
 * pour pouvoir stocker des contacts
 *
 * */

class MainActivity : AppCompatActivity() {

    var personnes = mutableListOf<Personne>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.stockageinternetp4.R.layout.activity_main)
        buildRecyclerView()


        /**
         * on recupére  le  contenu du fichier dans  une chaine

         de caractére que l'on va traiter par la suite
         **/
        if(File(this.filesDir,
                "unFichier.txt").exists()) {


        var recuperation = File(this.filesDir,
            "unFichier.txt").bufferedReader().readText()



        //une variable bool pour controler la fin du fichier
       var arret=false
       var ligne = recuperation

            /**si le fichier
             * est vide initalement ou qu'on a atteint la fin
             * ne pas entrer dans la boucle**/

            var car0 = 0
            if (ligne=="FIN"||ligne==""){

                arret=true
            }else {


                var car1 = ligne.indexOf(',', car0)
                var NOM = ligne.subSequence(car0, car1)
                var car2 = ligne.indexOf(',', car1 + 1)
                var EMAIL = ligne.subSequence(car1 + 1, car2)
                var car3 = ligne.indexOf(',', car2 + 1)
                var TEL = ligne.subSequence(car2 + 1, car3)
                var car4 = ligne.indexOf('/')

                var FIXE = ligne.subSequence(car3 + 1, car4)
                var FIN = ligne.subSequence(car4 + 1, car4 + 3)
                //on ajoute a la liste de contact
                if ((NOM is String) && (EMAIL is String) && (TEL is String) && (FIXE is String)) {

                    var p9 = Personne(NOM, EMAIL, TEL, FIXE)
                    personnes.add(0, p9)
                    personnes.sortWith(compareBy({ it.nom }))
                    buildRecyclerView()
                    mon_recycler.adapter?.notifyItemInserted(0)

                }


                //on boucle tant qu'on a quelque chosse a lire dans le fichier
                while((arret==false)&&(car0<ligne.length)) {
                    car0=car4+1
                    car1 = ligne.indexOf(',', car0)
                    NOM = ligne.subSequence(car0, car1)
                    car2 = ligne.indexOf(',', car1 + 1)
                    EMAIL = ligne.subSequence(car1 + 1, car2)
                    car3 = ligne.indexOf(',', car2 + 1)
                    TEL = ligne.subSequence(car2 + 1, car3)
                    car4 = ligne.indexOf('/',car3)

                    FIXE = ligne.subSequence(car3 + 1, car4)

                    FIN=ligne.subSequence(car4+1, car4+4)

                    if ((NOM is String) && (EMAIL is String) && (TEL is String) && (FIXE is String)) {

                        var p9 = Personne(NOM, EMAIL, TEL, FIXE)
                        personnes.add(0, p9)
                        personnes.sortWith(compareBy({ it.nom }))
                        buildRecyclerView()
                        mon_recycler.adapter?.notifyItemInserted(0)

                    }

                    if (FIN=="FIN"){
                        arret=true
                    }


                }
            }

            }
        //si le fichier n existe pas le créer et lui mettre le mot FIN pour dire q'il est vide
        else{

            File(this.filesDir, "unFichier.txt").outputStream().use {
                it.write("FIN".toByteArray())
            }

            }







        //le bouton pour permettre la saisie d'un contact
        btn_ajouter.setOnClickListener {

            startActivityForResult<AjoutPersonne>(1)


        }

    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            1 -> {
                // Résultat de startActivityForResult<ModifierActivity>
                if(resultCode == Activity.RESULT_OK){
                    val valider = data?.getBooleanExtra(AjoutPersonne.EXTRA_VALIDER, false) ?: false
                    if(valider){
                        // L'utilisateur a utilisé le bouton "valider"
                        // On récupère la valeur dans l'extra (avec une valeur par défaut de "")
                        val nouvValeurnom = data?.getStringExtra(AjoutPersonne.EXTRA_NOM) ?: ""
                        val nouvValeuremail = data?.getStringExtra(AjoutPersonne.EXTRA_EMAIL) ?: ""
                        val nouvValeurtel = data?.getStringExtra(AjoutPersonne.EXTRA_TEL) ?: ""
                        val nouvValeurfixe = data?.getStringExtra(AjoutPersonne.EXTRA_FAXE) ?: ""

                        var p8=Personne(nouvValeurnom,nouvValeuremail,nouvValeurtel,nouvValeurfixe)


                        //Ecriture dans un fichier interne contact.data pour enregistrer chaque
                        // contacts et le délimiteur ici est la , la fin d'ine ligne est /


                        /**
                         *on recup l'ancien contenu du fichier
                         * pour réecrire dessu sans perdre l'information
                         *
                         *
                         **/
                        val recuperation = File(this.filesDir,
                            "unFichier.txt").bufferedReader().readText()





                        var ajout=nouvValeurnom+","+nouvValeuremail+","+nouvValeurtel+","+nouvValeurfixe+"/"+recuperation

                        toast(ajout)
                        File(this.filesDir,"unFichier.txt").outputStream().use {
                            it.write((ajout).toByteArray())

                        }

                        personnes.add(0,p8)
                        //cette ligne permet de trier la liste des contactes par ordre alphabetique
                        personnes.sortWith(compareBy({it.nom}))
                        buildRecyclerView()
                        mon_recycler.adapter?.notifyItemInserted(0)

                    }else{
                        //ID--
                    }
                }else if(resultCode == Activity.RESULT_CANCELED){
                    // L'utilisateur a utilisé le bouton retour <- de son téléphone
                    // on ne fait rien de spécial non plus
                }
            }
        }
    }


    fun buildRecyclerView() {
        mon_recycler.setHasFixedSize(true)
        //mon_recycler.setAdapter(mAdapter)
        mon_recycler.layoutManager = LinearLayoutManager(this)

        mon_recycler.adapter = PersonneAdapter(personnes.toTypedArray())
        {
            //ici on affiche juste toutes les informations dans un Toast
            //on aurait tres bien pu les passer en parametre avec un intent et les afficher dans une autre activity
            Toast.makeText(this, "Element selectionné: ${it}", Toast.LENGTH_LONG).show()
            var  nom="${it.nom}"
            var  tel="${it.tel}"
            var  mail="${it.email}"
            var  faxe="${it.fixe}"
            val intent3 = Intent(this, AfficheDetailActivity::class.java)
            intent3.putExtra("NOM",nom)
            intent3.putExtra("TEL",tel)
            intent3.putExtra("MAIL",mail)
            intent3.putExtra("FAXE",faxe)
            startActivity(intent3)


        }


    }



}