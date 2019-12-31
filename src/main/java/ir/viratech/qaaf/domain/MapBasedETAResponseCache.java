package ir.viratech.qaaf.domain;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import ir.viratech.qaaf.domain.Node.Coordinate;


/**
 * ETAResponseCache
 */
public class MapBasedETAResponseCache implements ETAResponseCacheInterface, Serializable {

    private Map<NodePair, ETAResponse> cache;

    /**
     * @return the cache
     */
    public Map<NodePair, ETAResponse> getCache() {
        return cache;
    }

    public Kryo kryoFactory() {
        Kryo kryo = new Kryo();
        kryo.register(MapBasedETAResponseCache.class);
        kryo.register(java.util.HashMap.class);
        kryo.register(NodePair.class);
        kryo.register(Node.class);
        kryo.register(Coordinate.class);
        kryo.register(ETAResponse.class);
        return kryo;
    }

    @Override
    public ETAResponse getETA(Node from, Node to) {
        if (this.cache == null) {
            return null;
        } 
        NodePair pair = new NodePair(from, to);
       if (this.cache.containsKey(pair)) {
            return cache.get(pair);
        } else {
            return null;
        }
    }

    @Override
    public void addETA(Node from, Node to, ETAResponse eta) {
        if (this.cache == null) this.cache = new HashMap<>();
        NodePair pair = new NodePair(from, to);
        this.cache.put(pair, eta);
    }

    @Override
    public void saveTo(String path) {

        Kryo kryo = this.kryoFactory();
        
        Output output = null; 
        FileOutputStream fos = null;
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(path, "rw");
            fos = new FileOutputStream(raf.getFD());
            output = new Output(fos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            kryo.writeObject(output, this);
            output.close();
            fos.close();
            raf.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

	@Override
	public void loadFrom(String path) {
        System.out.println("in method loadFrom --> 1");

        Kryo kryo = this.kryoFactory();
        kryo.register(MapBasedETAResponseCache.class);
        
        Input input = null; 
        RandomAccessFile raf = null;
        FileInputStream fis = null;
        if(Files.notExists(Paths.get(path))) { 
            this.saveTo(path);
        }
        try {
            raf = new RandomAccessFile(path, "rw");
            fis = new FileInputStream(raf.getFD());
            System.out.println("in method loadFrom --> 2");
            input = new Input(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        MapBasedETAResponseCache cachedData = null;
        try {
            System.out.println("in method loadFrom --> 4");
            cachedData = (MapBasedETAResponseCache) kryo.readObject(input, this.getClass());
            System.out.println("in method loadFrom --> 5");
            input.close();
            fis.close();
            raf.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        this.cache = cachedData.cache;
	}    

}

class NodePair implements Serializable {
    Node from, to;
    public NodePair (Node from, Node to) {
        this.from = from;
        this.to = to;
    }

    public NodePair () {
        
    }

    @Override
    public int hashCode() {
        return (int) ((this.from.getStart() - this.to.getStart()) * 1000000);
    }

    @Override
    public boolean equals(Object obj) {
        NodePair pair = null;
        if (!(obj instanceof NodePair)) {
            return false;
        } 
        pair = (NodePair) obj;
        return (this.from.equals(pair.from) && this.to.equals(pair.to));
    }
}