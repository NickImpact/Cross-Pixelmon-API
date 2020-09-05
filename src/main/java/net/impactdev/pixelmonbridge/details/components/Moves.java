package net.impactdev.pixelmonbridge.details.components;

import com.google.gson.JsonElement;
import net.impactdev.pixelmonbridge.data.Writable;
import net.impactdev.pixelmonbridge.data.factory.JArray;
import net.impactdev.pixelmonbridge.data.factory.JObject;

public class Moves implements Writable<JArray> {

    private int size = 0;
    private final Move[] moves = new Move[]{null, null, null, null};

    public Moves(Move... moves) {
        for(int i = 0; i < 4 && i < moves.length; i++) {
            this.moves[i] = moves[i];
        }
    }

    public boolean append(Move move) {
        if(this.size == 4) {
            return false;
        }

        this.moves[this.size++] = move;
        return true;
    }

    public void set(int index, Move move) {
        if(this.moves[index] == null) {
            this.size++;
        }

        this.moves[index] = move;
    }

    public Move[] getMoves() {
        return this.moves;
    }

    public int getSize() {
        return this.size;
    }

    @Override
    public JArray serialize() {
        JArray array = new JArray();
        for(Move move : this.moves) {
            if(move == null) {
                continue;
            }

            JObject m = new JObject()
                    .add("id", move.getID())
                    .add("pp", move.getPP())
                    .add("ppLevel", move.getPPLevel());
            array.add(m);
        }
        return array;
    }

    public static class Move {

        private String id;
        private int pp;
        private int ppLevel;

        public Move(String id, int pp, int ppLevel) {
            this.id = id;
            this.pp = pp;
            this.ppLevel = ppLevel;
        }

        public String getID() {
            return this.id;
        }

        public int getPP() {
            return this.pp;
        }

        public int getPPLevel() {
            return this.ppLevel;
        }

    }

}
