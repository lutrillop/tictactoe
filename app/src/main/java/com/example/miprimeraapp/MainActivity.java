package com.example.miprimeraapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
//implementation 'com.google.firebase:firebase-database:19.1.0'


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    /// DATABASE
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("version");

    DatabaseReference turno = database.getReference("/turno");
    DatabaseReference P1score = database.getReference("/P1Score");
    DatabaseReference P2score = database.getReference("/P2Score");
    private Button[][] buttons = new Button[4][4];

    private boolean player1Turn =true;

    private int roundCount;

    private int player1Points;
    private int player2Points;

    private TextView textViewPlayer1;
    private TextView textViewPlayer2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        player1Points = 0;
        player2Points = 0;
        P1score.setValue(player1Points);
        P2score.setValue(player2Points);
        setContentView(R.layout.activity_main);
        defineFireBase();
        turno.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Boolean value = dataSnapshot.getValue(Boolean.class);
                if(value!=null){
                    MainActivity.this.player1Turn = value;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        textViewPlayer1=findViewById(R.id.text_view_p1);
        textViewPlayer2=findViewById(R.id.text_view_p2);

        for(int i = 0; i < 4; i++){
            for(int j = 0; j < 4; j++){
                String buttonID = "button_" + i + j;
                int resID =getResources().getIdentifier(buttonID, "id", getPackageName());
                buttons[i][j] = findViewById(resID);
                buttons[i][j].setOnClickListener(this);
                DatabaseReference juegoRef = database.getReference("juego/" +  i + "/" + j);
                juegoRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String value=dataSnapshot.getValue(String.class);
                        if (value!=null){
                            int i = Integer.parseInt(dataSnapshot.getRef().getKey());
                            int j = Integer.parseInt(dataSnapshot.getRef().getParent().getKey());
                            Log.e("i", "i: "+ i);
                            Log.e("j", "j: "+ j);
                            MainActivity.this.buttons[j][i].setText(value);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }

        Button buttonReset = findViewById(R.id.button_reset);
        buttonReset.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                resetGame();
            }
        });

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Float value = dataSnapshot.getValue(Float.class);
                if(value!=null){
                    Toast.makeText(MainActivity.this,"Version: " + value.toString(),Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    public void onClick(View v){
        if (!((Button) v).getText().toString().equals("")){
            return;
        }
        if(player1Turn){
            ((Button) v).setText("X");
        }else{
            ((Button) v).setText("O");
        }

        roundCount++;


        if(checkForWin()){
            if(player1Turn){
                player1Wins();
            }else{
                player2Wins();
            }
        }else if(roundCount == 16){
            draw();
        }else{
            turno.setValue(!player1Turn);
        }
    }

    private boolean checkForWin(){
        String[][] field = new String[4][4];

        for(int i = 0; i < 4; i++){
            for(int j = 0; j < 4; j++){
                field[i][j] = buttons[i][j].getText().toString();
            }
        }

        //firebase
        for (int i = 0; i < 4; i++){
            for (int j = 0; j < 4; j++){
                DatabaseReference juegoRef = database.getReference("juego/" +  i + "/" + j);
                juegoRef.setValue(field[i][j]);
            }
        }


        for (int i = 0; i < 4; i++){
            if(field[i][0].equals(field[i][1])
                    && field[i][0].equals(field[i][2])
                    && !field[i][0].equals("")){
                return true;
            }

            if(field[i][3].equals(field[i][1])
                    && field[i][3].equals(field[i][2])
                    && !field[i][3].equals("")){
                return true;
            }
        }

        for (int i = 0; i < 4; i++){
            if(field[0][i].equals(field[1][i])
                    && field[0][i].equals(field[2][i])
                    && !field[0][i].equals("")){
                return true;
            }

            if(field[3][i].equals(field[1][i])
                    && field[3][i].equals(field[2][i])
                    && !field[3][i].equals("")){
                return true;
            }
        }

        //cruzado izquierdo
        if(field[0][0].equals(field[1][1])
                && field[0][0].equals(field[2][2])
                && !field[0][0].equals("")){
            return true;
        };

        if(field[3][3].equals(field[1][1])
                && field[3][3].equals(field[2][2])
                && !field[3][3].equals("")){
            return true;
        };

        if(field[0][3].equals(field[1][2])
                && field[0][3].equals(field[2][1])
                && !field[0][3].equals("")){
            return true;
        };

        if(field[3][0].equals(field[1][2])
                && field[3][0].equals(field[2][1])
                && !field[3][0].equals("")){
            return true;
        };


        //cruzado derecho
        if(field[0][2].equals(field[1][1])
                && field[0][2].equals(field[2][0])
                && !field[0][2].equals("")){
            return true;
        };

        if(field[3][0].equals(field[1][1])
                && field[3][0].equals(field[2][0])
                && !field[3][0].equals("")){
            return true;
        };



        if(field[0][1].equals(field[1][2])
                && field[0][1].equals(field[2][3])
                && !field[0][1].equals("")){
            return true;
        };


        if(field[1][0].equals(field[2][1])
                && field[1][0].equals(field[3][2])
                && !field[1][0].equals("")){
            return true;
        };

        //ultima diagonal invertida
        if(field[0][2].equals(field[1][1])
                && field[0][2].equals(field[2][0])
                && !field[0][2].equals("")){
            return true;
        };


        if(field[1][3].equals(field[2][2])
                && field[1][3].equals(field[3][1])
                && !field[1][3].equals("")){
            return true;
        };



        return false;
    }

    private void player1Wins(){
        player1Points++;
        Toast.makeText(this, "Player 1 Wins!", Toast.LENGTH_SHORT).show();
        P1score.setValue(player1Points);
        updatePointsText();
        resetBoard();
    }

    private void player2Wins(){
        player2Points++;
        Toast.makeText(this, "Player 2 Wins!", Toast.LENGTH_SHORT).show();
        P2score.setValue(player2Points);
        updatePointsText();
        resetBoard();
    }

    private void defineFireBase(){

        for (int i = 0; i < 4; i++){
            for (int j = 0; j < 4; j++){
                DatabaseReference juegoRef = database.getReference("juego/" +  i + "/" + j);
                juegoRef.setValue("");
            }
        }
    }


    private void draw(){
        Toast.makeText(this, "Draw!", Toast.LENGTH_SHORT).show();
        resetBoard();
    }

    private void updatePointsText(){
        textViewPlayer1.setText("Player 1: " + player1Points);
        textViewPlayer2.setText("Player 2: " + player2Points);
    }

    private void resetBoard(){
        for(int i = 0; i<4; i++){
            for(int j=0;j<4;j++){
                buttons[i][j].setText("");
            }
        }
        //reset firebase
        for (int i = 0; i < 4; i++){
            for (int j = 0; j < 4; j++){
                DatabaseReference juegoRef = database.getReference("juego/" +  i + "/" + j);
                juegoRef.setValue("");
            }
        }
        roundCount=0;
        turno.setValue(true);
    }

    private void resetGame(){
        player1Points = 0;
        player2Points = 0;
        P1score.setValue(player1Points);
        P2score.setValue(player2Points);

        updatePointsText();
        resetBoard();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("RoundCount", roundCount);
        outState.putInt("Player1Points ", player1Points);
        outState.putInt("player2Points", player2Points);
        outState.putBoolean("player1Turn", player1Turn);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        roundCount = savedInstanceState.getInt("RoundCount");
        player1Points = savedInstanceState.getInt("player1Points");
        player2Points = savedInstanceState.getInt("player2Points");
        turno.setValue(savedInstanceState.getBoolean("player1Turn"));

    }
}
