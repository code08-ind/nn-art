## Clustering by Adaptive Resonance Theory (ART)

[Adaptive resonance theory (ART)](https://en.wikipedia.org/wiki/Adaptive_resonance_theory) 
describes several neural network models for pattern recognition and prediction.
This project is using ART1 neural network type for clustering black & white images as described in the attached [documentation](doc.pdf).

The neural network is sensitive to "vigilance" parameter. 
Higher vigilance produces highly detailed clusters, while lower vigilance results in more general clusters.

The program is expecting black & white PNG pictures in RGBA representation, where only the alpha (4th bit) determines whether the image is black or white.

### Usage

Use `ant` to compile and run the program's GUI.

```
ant clean
ant compile  (includes clean)
ant jar      (includes compile)
ant run      (includes jar)
ant          (same as ant run)
```

### Note
The project was created as a homework within Soft Computing course at Faculty of Information Technology, Brno University of Technology, 2014.
