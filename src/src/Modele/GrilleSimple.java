package Modele;

import javax.swing.*;
import java.awt.*;
import java.util.Set;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Observable;


public class GrilleSimple extends Observable implements Runnable {

    public boolean enCours = false;

    public boolean enPause = false;

    public boolean jeuFini = false;

    public final int TAILLEY = 20;

    public final int TAILLEX = 10;

    public int score = 0;

    public int nbLignes = 0;

    private Piece pieceCourante = new Piece(this);

    public Piece nextPiece = new Piece(this);

    public Color[][] mySavingMap = new Color[TAILLEX][TAILLEY];

    public GrilleSimple() {

        new OrdonnanceurSimple(this).start(); // pour changer le temps de pause, garder la référence de l'ordonnanceur
        
    }

    public void demarrer(){
        enCours = true;
    }

    public void pause(){
        if(enPause)enPause = false;
        else enPause = true;
    }

    public void action(int keycode) {
        pieceCourante.action(keycode);
    }

    public boolean validationPosition(boolean[][] piece, int _nextX, int _nextY) {

        boolean valide = true;

        int xAbs; int yAbs;

        for(int j = 0; j < 4; j++)
        {
            for(int i = 0; i < 4; i++)
            {
                xAbs = _nextX + i;
                yAbs = _nextY + j;

                if((xAbs > TAILLEX -1 || xAbs < 0 || yAbs > TAILLEY -1 || yAbs < 0 || mySavingMap[xAbs][yAbs] != null) && piece[i][j]) {// {mySavingMap[xAbs][yAbs]){
                    valide = false;
                    break;
                }
            }
            if(!valide) break;
        }

        if(valide && _nextY != pieceCourante.gety()) score +=1;
        return valide;
    }

    /* fonction qui se charge de faire descendre le tetris quand des lignes sont pleines */
    public void effacerLignes(){
        boolean lignePleine;
        int nbLignesTour = 0;
        for(int j = TAILLEY-1; j >= 0; j--){
            lignePleine = true;

            for(int i = TAILLEX-1; i >= 0; i--){
                if(mySavingMap[i][j] == null){
                    lignePleine = false;
                    break;
                }
            }

            if(lignePleine){
                for(int sy = j; sy > 0; sy--){
                    for(int l = TAILLEX-1; l >= 0; l--){
                        mySavingMap[l][sy] = mySavingMap[l][sy-1];
                    }
                }
                j += 1;
                nbLignesTour += 1;
            }
        }
        nbLignes = nbLignesTour;
        /* points bonus lorsque plusieurs lignes sont supprimées d'un coup */
        switch(nbLignesTour){
            case 1: score += 40; break;
            case 2: score += 100; break;
            case 3: score += 300; break;
            case 4: score += 1200; break;
        }
    }

    public void cleanMap()
    {
        for(int i = 0; i < TAILLEX; i ++)
        {
            for (int j = 0; j < TAILLEY; j++)
            {
                mySavingMap[i][j] = Color.BLACK;
            }
        }
        pieceCourante.couleur = Color.BLACK;
    }


    public void placerDansGrille(int x, int y){

        int xAbs; int yAbs;

        for(int i = 0; i < 4; i++) {
            for(int j = 0; j < 4; j++){
                xAbs = pieceCourante.getx() + i;
                yAbs = pieceCourante.gety() + j;

                /* On rempli les cases de la grille de stockage en fonction de la couleur de la case coloriée */
                if(pieceCourante.motif[i][j]) mySavingMap[xAbs][yAbs] = pieceCourante.getColor();
            }
        }

        effacerLignes();
    }

    public void run() {
        if(enCours && !enPause){
            pieceCourante.run();
            if (pieceCourante.PiecePlacee) {
                if (!jeuFini) {
                    if (pieceCourante.gety() <= 0) {
                        jeuFini = true;
                        JFrame f = new JFrame("message");
                        JLabel j = new JLabel("Game Over");
                        JPanel p = new JPanel();
                        p.add(j);
                        f.add(p);
                        f.setSize(300,100);
                        f.show();
                        cleanMap();
                    } else {
                        pieceCourante = nextPiece;
                        nextPiece = new Piece(this);
                    }
                }
            }


            setChanged(); // setChanged() + notifyObservers() : notification de la vue pour le rafraichissement
            notifyObservers();
        }
    }


    public Piece getPieceCourante() {
        return pieceCourante;
    }

}
