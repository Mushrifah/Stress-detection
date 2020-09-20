import cv2
import numpy as np
import matplotlib.pyplot as plt

img1= cv2.imread('test1.jpg')
img1=cv2.resize(img1,(512,512))
img= cv2.imread('test1.jpg',0)
img=cv2.resize(img,(512,512))
dst = cv2.fastNlMeansDenoising(img,10,10,7,21)
sub=img[256:256+150,256:256+150]
avg_color = np.average(sub, axis=0)
avg_color = np.average(avg_color, axis=0)
print(avg_color)
if((avg_color-100)<=15):
     _,mask=cv2.threshold(img,100,255,cv2.THRESH_BINARY_INV)
     res=cv2.bitwise_and(img1,img1, mask=mask)
     cv2.imshow('result',res)
else:
    _,mask=cv2.threshold(img,100,255,cv2.THRESH_BINARY)
    res=cv2.bitwise_and(img1,img1, mask=mask)
    cv2.imshow('result',res)

cv2.imshow('img',img1)
cv2.waitKey(0)



