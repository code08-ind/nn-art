## ART neural network for clustering of B&W pictures

Clustering of binary input vectors depending on vigilance 
parameter (limit of similarity) to in advance unknown number of classes.

Program is processing black&white PNG pictures in RGBA representation 
(getting each 4th bit only to check B/W).

Program was created within course Soft Computing at Faculty of Information Technolog, BUT, 2014.

#### Project structure
```
+ lib	          ...  Collections of images (simple and vehicles)
+ src             ...  Source files of this project
+ out/production  ...  Output directory for *.class
+ out/jar         ...  Output directory for art.jar runnable archive  
- README.txt      ...  This file 
- doc.pdf         ...  Technical report of project
- build.xml       ...  Build instructions for ant tool
```

### III. Usage
Using Ant tool.
```
ant clean
ant compile  (includes clean)
ant jar      (includes compile)
ant run      (includes jar)
ant          (same as ant run)   
```


