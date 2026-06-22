package com.example.myapplication

import android.widget.EditText
import androidx.contentpager.content.Query

enum class EtatDemande {
    DWMANDE_RECUE,
    EN_ETUDE,
    ACCEPTEE,
    REFUSEE
}


class DemandeInvalideException(message:String):Exception(message)


data class Demande(
    val numeroDemande:Int,
    val nomDemandeur:String,
    val dateDepot:String,
    var etat:EtatDemande)


class GestionDemandes(){
    val list : ArrayList<Demande> = arrayListOf()

    fun ajouterDemande(demande:Demande){
        list.add(demande)
    }

    fun trouverDemande(id: Int): Demande?{
        val domand = list.find{it.numeroDemande == id}
        return domand
    }

    fun rechercherParStatut(etat: EtatDemande):List<Demande>{
        val listdomand = list.filter{it.etat == etat}
        return listdomand
    }

    fun supprimerDemande(id:Int):Boolean{
        val suppdomand = trouverDemande(id)
        return list.remove(suppdomand)
    }


    fun  changerEtat(id:Int, nouvelEtat: EtatDemande){
        val changerdomand = trouverDemande(id)

        if(changerdomand != null){
            changerdomand.etat = nouvelEtat
        }else{
            throw DemandeInvalideException("mal9ahach")
        }


    }

    fun afficherToutesLesDemandes(){
        if(list.size > 0){
            list.forEach{"Numero Demande: ${it.numeroDemande} "+
                    "\nNom Demandeur: ${it.nomDemandeur}"+
                    "\nDate Depot: ${it.dateDepot}"+
                    "\nEtat: ${it.etat}"
            }
        }else{
            throw DemandeInvalideException("makaina 7ta domand")
        }
    }

}

fun main(){
    val demande1 = Demande(1,
        "yassin",
        "10/06/2026",
        EtatDemande.REFUSEE)
    val demande2 = Demande(2,
        "tarik",
        "08/11/2026",
        EtatDemande.DWMANDE_RECUE)

    val gestionDemandes = GestionDemandes()

    gestionDemandes.ajouterDemande(demande1)
    gestionDemandes.ajouterDemande(demande2)

    print(gestionDemandes.trouverDemande(3))
    print(gestionDemandes.trouverDemande(1))
    print("Supp: ${gestionDemandes.supprimerDemande(1)}")
    print(gestionDemandes.trouverDemande(1))

    gestionDemandes.changerEtat(2, EtatDemande.ACCEPTEE)

    print(gestionDemandes.afficherToutesLesDemandes())




}


@Entity
data class Produit(
    @PrimaryKey(autoGenerate = true)
    val id : Int=0,
    val titre : String,
    val prix : Double,
    val image : String
)

@Dao
interface ProduitDao{
    @Insert
    fun  insererProduit(produit: Produit)

    @Query("SELECT * FROM produit")
    fun getToustProduits():List<Produit>

    @Delete
    fun supprimerProduit(produit: Produit)

    @Update
    fun updateProduit(produit: Produit)
}

setContentView(R.layout.layout)

val titre = findViewById<EditText>(R.id.titre)
val prix = findViewById<EditText>(R.id.prix)
val image = findViewById<EditText>(R.id.image)
val btn = findViewById<Button>(R.id.btn)

btn.setOnClickListener{
    val produit1 = Produit(titre= titre.text.toString(), prix= prix.text.toString.toDouble(), image= image.text.toString())

}

