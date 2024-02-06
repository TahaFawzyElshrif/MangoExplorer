package com.example.mangoexplorer;


import java.util.*;
import java.util.stream.Collectors;


public class queue{
    private String[][] data;
    private int indx=0;
    public queue(int length){
        data= new String[length][];
    }

    public void enque(String[] s){
        data[indx]=s;
        indx=(indx+1)%data.length;
    }
    public void deque(){
        //won't used
    }

    public String[][] getData() {
        return data;
    }
    public String[][] printOrdered(){
        String[][] ordered=new String[data.length][];
        int indx_cpy=indx;//to prevent moving main pointer
        for(int i=0;i<data.length;i++){
            ordered[i]=data[indx_cpy];
            indx_cpy=(indx_cpy+1)%data.length;

        }
        return removeNulls(ordered);
    }
    private String[][] removeNulls(String[][] ordered) {
        List<String[]> list = new ArrayList<>();
        for (String[] element : data) {
            if (element != null) {
                list.add(element);
            }
        }
        return list.toArray(Arrays.copyOf(data, 0));
    }

    public Map<String[], Integer> freqMap() {
        /*use:
            Map<String,Integer> sorted=sortByValue(o.freqMap());
            for(String i:sorted.keySet()){
                if( i!=null){
                System.out.println(i+" : "+sorted.get(i));
                }
            }

        * *//*
        Map<String[], Integer> map = new HashMap<>();
        for (String[] elemT:data){
            map.put(elemT, map.getOrDefault(elemT, 0) + 1);
        }*/
        Map<List<String>, Integer> map = new HashMap<>();
        for (String[] elemT : data) {
            if(elemT!=null) {
                List<String> keyList = Arrays.asList(elemT);
                map.put(keyList, map.getOrDefault(keyList, 0) + 1);
            }
        }


        Map<String[],Integer> newFormattedMap=new HashMap<>();
        for (List<String> elemT : map.keySet()) {
            if(elemT!=null) {
                newFormattedMap.put(new String[]{elemT.get(0), elemT.get(1)}, map.get(elemT));
            }
        }

        return newFormattedMap;
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        return map.entrySet()
                .stream()
                .sorted(Map.Entry.<K, V>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

}