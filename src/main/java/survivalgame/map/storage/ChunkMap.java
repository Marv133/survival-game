package survivalgame.map.storage;

class ChunkMap {

    private static final int CHUNK_SIZE = 32;

    private int[] chunk;
    private float[] environment; //todo überdenken ob das so machbar ist

    public ChunkMap(){

    }


    public int getAt(int x, int z){
        return chunk[x + z*CHUNK_SIZE];
    }

}
