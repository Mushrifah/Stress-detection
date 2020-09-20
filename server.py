import flask
import random

import sys
import os
import glob
import re
from pathlib import Path
import pickle
import numpy as np

# Import fast.ai Library
from fastai import *
from fastai.vision import *

# Flask utils
from flask import Flask, redirect, url_for, request, render_template,jsonify
from werkzeug.utils import secure_filename

app = flask.Flask(__name__)
UPLOAD_FOLDER = './UPLOAD_FOLDER/'

path=Path("path")

classes = ['stress', 'non-stress']

learn=load_learner(path,'a.pkl')


with open('classifier_pickle','rb') as f:
    cls=pickle.load(f)
label_dictionary = {0: 'Healthy Plant', 1: 'Stress but recoverable',2:'Cannot Recover'}

def model_predict(img_path):
    """model_predict will return the preprocessed image
    """
    img = open_image(img_path)
    pred_class,pred_idx,outputs = learn.predict(img)
    return pred_class

@app.route('/upload', methods = ['GET', 'POST'])
def handle_request():
    print("hello");
    imagefile = flask.request.files['image']
    print("hello", flask.request);
    filename = UPLOAD_FOLDER + str(random.randint(0, 5000)) + '.png'
    #filename = werkzeug.utils.secure_filename(imagefile.filename)
    #filename= "photo.jpg";
    print("\nReceived image File name : " + imagefile.filename)
    imagefile.save(filename)

    preds=model_predict(filename)
    print(type(preds))

    return str(preds)

@app.route('/calculate', methods = ['GET', 'POST'])
def handle_response():
	print("Hello");
    # getting the data from a separate json file.
	json = request.get_json()
    
    # the keys that should be included in the json file.
	transaction_keys = ['tdry' , 'twet', 'tcanopy', 'timeDay']
    
    # return a error message if a key is not included in the file.

	#stringValues= flask.request.values.get['dry', 'wet', 'canopy', 'time']
	#print("Hello", flask.request);
	a=json[transaction_keys[0]]
	print(a)
	b=json[transaction_keys[1]]
	print(b)
	c=json[transaction_keys[2]]
	print(c)
	d=json[transaction_keys[3]]
	print(d)
	pred=np.array([[a,b,c,d]])
	pr=cls.predict(pred)
	print(pr)
	return jsonify(label_dictionary[int(pr)])
	#ans=label_dictionary[int(pr)]
	#print(ans)
	#return ans

app.run(host="127.0.0.1",port=5000, debug=True)