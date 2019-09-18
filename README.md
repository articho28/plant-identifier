# AI for Climate Change Hackathon

This repo contains the work my team and I have done in the context of the AI for Climate Change Hackathon that took place at MILA from September 13 to 15 2019. 

Our team trained a convolutional neural network to identify plant deseases from a dataset of over 44 000 thousand images. 

The aim was to deploy the application on mobile simulate real-time classification from a drone camera feed. 

We have successfully trained our own model, but ultimately opted to retrain Tensorflow's mobilenetv2 to make a lightweight model and deploy quickly. 

This repo contains all of our models as well as the template android app (source: Tensorflow examples). 

The dataset can be downloaded at https://www.dropbox.com/s/7jvh6aeheja9qar/plant-original-dataset.zip?dl=1

Source: PlantVillage

Team: Ayoub El-Hanchi, Antoine Frau, and Artsiom Skliar


## initial-model

This folder contains the CNN we built from scractch using the Keras library. We have reached an accuracy of 92% on the training set and 80% on the validation set. 

## transfer-learning-apples

This folder contains a retrained mobilenetv2 model trained to only recognize the apple specimens from the provided dataset.

## transfer-learning-full-dataset

Once we successfully retrained the mobilenetv2, we attempted to train it on the full datasets.

## andrdoid 

This folder contains the app template to launch and deploy the tflite model of choice. 
