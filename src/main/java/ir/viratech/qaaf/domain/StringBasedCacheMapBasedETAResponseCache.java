// package ir.viratech.qaaf.domain;

// import java.io.FileInputStream;
// import java.io.FileNotFoundException;
// import java.io.FileOutputStream;
// import java.io.IOException;
// import java.io.ObjectInputStream;
// import java.io.ObjectOutputStream;
// import java.io.PrintWriter;
// import java.io.RandomAccessFile;
// import java.io.Serializable;
// import java.nio.file.Files;
// import java.nio.file.Paths;
// import java.util.HashMap;
// import java.util.Map;
// import java.util.Scanner;

// /**
//  * StringBasedCacheMapBasedETAResponseCache
//  */
// public class StringBasedCacheMapBasedETAResponseCache implements ETAResponseCacheInterface {

//     /**
//      *
//      */
//     private static final long serialVersionUID = 1L;
//     private Map<NodePair, ETAResponse> cache;

//     @Override
//     public ETAResponse getETA(Node from, Node to) {
//         if (this.cache == null) {
//             return null;
//         } 
//         NodePair pair = new NodePair(from, to);
//        if (this.cache.containsKey(pair)) {
//             return cache.get(pair);
//         } else {
//             return null;
//         }
//     }

//     @Override
//     public void addETA(Node from, Node to, ETAResponse eta) {
//         if (this.cache == null) this.cache = new HashMap<>();
//         NodePair pair = new NodePair(from, to);
//         this.cache.put(pair, eta);
//     }

//     @Override
//     public void saveTo(String path) {
//         RandomAccessFile raf = null;
//         FileOutputStream fos = null;
//         PrintWriter pw = null;
//         try {
//             raf = new RandomAccessFile(path, "rw");
//             fos = new FileOutputStream(raf.getFD());
//             pw = new PrintWriter(fos);
//         } catch (FileNotFoundException e) {
//             e.printStackTrace();
//         } catch (IOException e) {
//             // TODO Auto-generated catch block
//             e.printStackTrace();
//         }
//         try {
//             for (NodePair pair : this.cache.keySet()) {
//                 pw.println(pair.from);
//                 pw.println(pair.to);
//                 pw.println(this.cache.get(pair).getDistance());
//                 pw.println(this.cache.get(pair).getDuration());
//                 pw.println(this.cache.get(pair).getDurationInTraffic());
//                 pw.println(this.cache.get(pair).getGeometry());
//             }
//             pw.close();
//             fos.close();
//             raf.close();
//         } catch (IOException e) {
//             // TODO Auto-generated catch block
//             e.printStackTrace();
//         }
//     }

// 	@Override
// 	public void loadFrom(String path) {
//         System.out.println("in method loadFrom --> 1");
//         if (this.cache == null)
//             this.cache = new HashMap<>();
//         FileInputStream fis = null;
//         Scanner scanner = null;
//         RandomAccessFile raf = null;
//         if(Files.notExists(Paths.get(path))) { 
//             this.saveTo(path);
//         }
//         try {
//             raf = new RandomAccessFile(path, "rw");
//             fis = new FileInputStream(raf.getFD());
//             System.out.println("in method loadFrom --> 2");
//             scanner = new Scanner(fis);
//         } catch (FileNotFoundException e) {
//             e.printStackTrace();
//         } catch (IOException e) {
//             // TODO Auto-generated catch block
//             e.printStackTrace();
//         }
//         MapBasedETAResponseCache cachedData = null;
//         try {
//             System.out.println("in method loadFrom --> 4");
//             String[] fromStr, toStr;
//             String distanceStr, durationStr, durationInTrafficStr, geometryStr;
//             while (scanner.hasNextLine()) {
//                 fromStr = scanner.nextLine().split(",");
//                 toStr = scanner.nextLine().split(",");
//                 distanceStr = scanner.nextLine();
//                 durationStr = scanner.nextLine();
//                 durationInTrafficStr = scanner.nextLine();
//                 geometryStr = scanner.nextLine();

//                 Node from = new Node(), to = new Node();
                
//                 from.getCoordinate().setLng(Double.parseDouble(fromStr[0]));
//                 from.getCoordinate().setLat(Double.parseDouble(fromStr[1]));
//                 to.getCoordinate().setLng(Double.parseDouble(toStr[0]));
//                 to.getCoordinate().setLat(Double.parseDouble(toStr[1]));
                
//                 NodePair pair = new NodePair(from, to);
//                 ETAResponse eta = new ETAResponse();

//                 eta.setDistance(Integer.parseInt(distanceStr));
//                 eta.setDuration(Integer.parseInt(durationStr));
//                 eta.setDurationInTraffic(Integer.parseInt(durationInTrafficStr));
//                 eta.setGeometry(geometryStr);

//                 this.cache.put(pair, eta);
//             }
//             System.out.println("in method loadFrom --> 5");
//             scanner.close();
//             fis.close();
//             raf.close();
//         } catch (IOException e) {
//             // TODO Auto-generated catch block
//             e.printStackTrace();
//         }
//     }    

//     public static void main(String[] args) {
//         StringBasedCacheMapBasedETAResponseCache cache = new StringBasedCacheMapBasedETAResponseCache();
//         MapBasedETAResponseCache cache2 = new MapBasedETAResponseCache();
//         System.out.println(1);
//         cache2.loadFrom("CachedETAResponses");
//         System.out.println(2);
//         cache.cache = cache2.getCache();
//         cache.saveTo("FastCache");
//     }

// }

// class NodePair implements Serializable {
//     /**
//      *
//      */
//     private static final long serialVersionUID = 1L;
//     Node from, to;
//     public NodePair (Node from, Node to) {
//         this.from = from;
//         this.to = to;
//     }

//     @Override
//     public int hashCode() {
//         return (int) ((this.from.getStart() - this.to.getStart()) * 1000000);
//     }

//     @Override
//     public boolean equals(Object obj) {
//         NodePair pair = null;
//         if (!(obj instanceof NodePair)) {
//             return false;
//         } 
//         pair = (NodePair) obj;
//         return (this.from.equals(pair.from) && this.to.equals(pair.to));
//     }
// }