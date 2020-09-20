# Importing the libraries
import numpy as np
import pandas as pd
import pickle



# Importing the dataset
dataset = pd.read_csv('tir.csv')
X = dataset.iloc[:, [0,1,2,4]].values
y = dataset.iloc[:, 5].values

# Splitting the dataset into the Training set and Test set
from sklearn.model_selection import train_test_split
X_train, X_test, y_train, y_test = train_test_split(X, y, test_size = 0.20, random_state = 0)





# Fitting SVM to the Training set
from sklearn.svm import SVC
classifier = SVC(kernel ='linear', random_state = 0)
classifier.fit(X_train, y_train)


# Predicting the Test set results
y_pred = classifier.predict(X_test)


with open('classifier_pickle','wb') as f:
    pickle.dump(classifier,f)