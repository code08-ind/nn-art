===========================================
  Black and white pictures classification 
        by ART neural network
===========================================

Project: VUT FIT Soft computing 2014
Author:  Martin Veselovský, xvesel60
Email:   xvesel60@stud.fit.vutbr.cz


I. Description
===============
Classification of binary input vectors depending on vigilance
parameter (limit of similarity) to in advance unknown number of classes.
Program is processing black and white PNG pictures in RGBA
representation (getting each 4th bit only to check B/W).

II. Structure
==============
+ lib	            ...  Collections of images (simple and vehicles)
+ src             ...  Source files of this project
+ out/production  ...  Output directory for *.class
+ out/jar         ...  Output directory for art.jar runnable archive  
- README.txt      ...  This file 
- doc.pdf         ...  Technical report of project
- build.xml       ...  Build instructions for ant tool

III. Usage
==========
Ant tool usage:

  ant clean
  ant compile  (includes clean)
  ant jar      (includes compile)
  ant run      (includes jar)
  ant          (same as ant run)   



