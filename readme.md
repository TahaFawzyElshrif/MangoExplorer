# Mango File Explorer (V 1.0.0)

Mango file explorer is a small project (simple edition) of fishoned file explorer app for android that have some useful features


## requirements
at least android 7.0 , 
**have not been tested for android 12+ and may not work**
## What App Do:
- file explorer with features :add /remove /get details /share
- display recent opened files (**recent**) ,with max 20 
- display frequently opened files (**freq/frequently**) ,with max 100
- set favourite files (**favourite/starred**) ,with max 150 
- fashioned app with themes and animation and small size
- tested layout on low pixels device and good (responsive UI)

## how does App work:
- app written totally with java 8
### explorer (explorer activity):
- all premessions  and was from TedPermission
- at first call the root element ( represent main directory ,just named files) and it to the layout in the middle
```
 LinearLayout root_item = Utils.getItem(explorer.this, "Files", R.drawable.folder);
        layout_middle.addView(root_item);
```

- utils is a class for frequentely ,general  methods or make some methods abstract to make it easy to organise code ,also continans static variable that represent general app case ,like theme ,and constants (final static) to make strings for example not hard written every time used
- utils.addItem is just create layout eith name give and specify image as :
if folder :
```
R.drawable.folder
```
if file :
```
Utils.getFileImage(i)
```
that take a file and determine drawable from it's extension **after last dot so in some unusaul folders may not be the best**


- then setContet_layout_middle that take root folder "/storage/emulated/0/" and list it's files **sorted in the typeOfSort way**
and set recursively click on the next files (was called in checkpremession)
```
for (File i : Utils.sorted(rootDirectory.listFiles())) {
                            LinearLayout child_item;
                            if (i.isDirectory()) {
                                child_item = Utils.getItem(explorer.this, i.getName(), R.drawable.folder);
                            } else {
                                child_item = Utils.getItem(explorer.this, i.getName(), Utils.getFileImage(i));
                            }
                            setLongSelecting(child_item,i);
                            child_item.setId(View.generateViewId());//to be unique


                            layout_middle.addView(child_item);
                            setContet_layout_middle(i, child_item);
                        }
```


- refresh and back are work same way ,but using the guidness of path from path TextView

- very similiar part used between refresh/back button so to solve it ,both get path (refresh)/modify (back) and then call refreshLayout()

- long selection is done by:
```
  int selected_item_id=-10000; //any number can't be id
    String  path_selected;//--->at end of path it have file name
    File selected_file;
```
those 3 are used to select file ,first one is used to select the id of selected layout ,and second 2 for files (can be optimized in next versions),the ids of layouts are set by java to remain unique whenever view created 
```
child_item.setId(View.generateViewId());//to be unique
```
each view use this method 
 ```
setLongSelecting(LinearLayout Item,File file) 
```
that when long selected set variables to value ,this is benefit if ypu share ,remove ,get details of file

- details of files :
this details are seen in AlertDialog.Builder that use dialog_info layout to display info


### Recent ,Freq, Starred
- all recent ,freq, starred are circular queue of some length ,to see actual elements printOrdered()
- reason of being limited size to prevent taking much space 
- but unfortantely it's not good for starring ,may be solved next version
- whenever file opened it added to recent and frequently
- all data are saved in private files csv 
- files are in form 
```
name.apk ,emulated1/
name1.apk ,emulated1/2/
```
and loaded as queue(String[])
- when opening frequently it make a map of file and how many it repeated then sort on reptition 
```
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
```
- the reason of this bad 2 loops is the fact that when just using one in Map<String[],Integer> and putting String[] as key ,it not work as one seperate key due to java

- Utils.getQueue is used to make it easy in the activity to load queue from the csv file ,parameter length may be deleted later
- Utils.addToQueueAndSave is used when recently /freq ,when new file opened apened it 
```
public static void  addToQueueAndSave(Context context,String fileName, queue queue,String[] item) {
        queue.enque(item);
        try {
            BufferedWriter fos = new BufferedWriter(new FileWriter(context.getFilesDir() + "/" + fileName, true));
            fos.write(item[0]+","+item[1]);
            fos.newLine();
            fos.close();
        } catch (Exception e) {
            Log.e("error",e.toString());

        }

    }
```
- all starred /recent/freq are shown in ShowQueue and activity that it determine which to show

### Application settings
- application use shared prefrences (some code in utils ,and load in main activity) that contain theme used and sort type

## libraries:
- TedPermission

## bugs and problems:
- some times refresh/back button get unexpected path
- way of using sort to replace each time one folder opened also animation ,and appending to recently with saving to files may making lag 
- current version not contain unstarring item feature
- fixed length of queue may solve Memeory problem but not storage ,also when full queue it may take the memory







# Updated V 1.5

## New Features :
 - Compression Advantage with different Techniques to Text Files
 - Performance mode that stop some nunecessary algorithms (sort, Animation) to perform faster 
- Automatic performance mode when low battery

### Details of New Features 
1- Compression 

## Algorithms
- Worked with Faculty Team to  implemented Compression techniques (Huffman ,Golamb,LZW,Arthimetric,Run Length) 


## Structure :
- for simplicity algorithms are made in python (and interacted using chaquopy)
- to actually interact and send numbers : each algorithm has it's on java class
- All classes extend algorithm class
- methods in algorithm class are not meant for abstract ,but are Utils commonly used in Compression (to make better code)
- for better code each class name is known in static variable of algorithm class

## Cycle :
- First of all To Compress : file is readed as text (StringBuffer **SomeTimes converted to String ,which may case problems**)
```
        StringBuffer data_file = Algorithm.readFile(fileToCompress);

```
- Second File is Read and preapared using it's custom class 
- Third Send to Python Class to be compressed
- all results converted to bits **Except for Arthimetric ,just produce json file** ,that stored it self as (each bits chunk stored as int)
- Prefix bits with 1 (to prevent loss if bits start with 0) than complete byties with 0s
- Same Cycle for Decompress (but bottom Up)

##### Team Worked in Compression :
- Walid Tawfik soliman
- Mazen Mohammed Montiser
- Reem Ibrahim
- Mohammed Elshaffay
- Sondos Khamis
- Shahd Khalaf

2- Performance
It uses static boolean, Shared Reference Variable (better_performance) :
- when Equal True Stop sorting and animation (always checked)
- When battery is Low make it true
- When battery is Charging : Make it False
- For End User :can stop ,apply it anytime




## Added libraries:
- com.fasterxml.jackson 2.13.0
- com.chaquo.python 15.0.1

## bugs Solved
- pefrormance mode helped making application faster (by stopping Animation and Sort)
- Solved Bug of unexpected redirect in refresh/back

## Bugs in Version 1.5
- Implemented premession  for reading files using READ_EXTERNAL_STORAGE,WRITE_EXTERNAL_STORAGE which may not work for high ndroid versions as described by google
- Compression is limited to text files 
- Compression may give sometimes unexpected behaviour ,may also negatively increase size
- UnFamiliarity in compressed extension
- Lossy Convert from StringBuffer to String
- For Huffman ,arthimetric : common to increase file size (although propotional)
- Some text files may not supported (C ,java…..)
- May get strickled for non ASCII files

## My email:
tahaelshrif1@gmail.com
