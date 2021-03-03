import numpy as np
import cv2
from datetime import *
import utils

cap = cv2.VideoCapture('assets/vtest.avi')

userStart = time(hour = 9, minute = 0)
userEnd = time(hour = 15, minute = 0)
recordTime = time(hour =12, minute = 30)

timeInRange = utils.checkTime(userStart, userEnd, recordTime)

sizeScore = 0
count = 0


#fgbg = cv2.createBackgroundSubtractorMOG2()
#kernel = cv2.getStructuringElement(cv2.MORPH_ELLIPSE, (3, 3))

firstFrame = None

while True:
    ret, frame = cap.read()

    if frame is None:
        break
        
    #fgmask = fgbg.apply(frame)
    #fgmask = cv2.morphologyEx(fgmask, cv2.MORPH_OPEN, kernel); 

    gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
    gray = cv2.GaussianBlur(gray, (21, 21), 0)
	# if the first frame is None, initialize it
    if firstFrame is None:
	    firstFrame = gray
	    continue

    # compute the absolute difference between the current frame and
	# first frame
    frameDelta = cv2.absdiff(firstFrame, gray)
    thresh = cv2.threshold(frameDelta, 25, 255, cv2.THRESH_BINARY)[1]
	# dilate the thresholded image to fill in holes, then find contours
	# on thresholded image
    thresh = cv2.dilate(thresh, None, iterations=2)


    count += 1
    frameScore = 0
    numObject = 0

    (contours, hierarchy) = cv2.findContours(thresh.copy(), cv2.RETR_EXTERNAL,cv2.CHAIN_APPROX_SIMPLE)
    for c in contours:
            if cv2.contourArea(c) < 500:
                continue

            frameScore += cv2.contourArea(c)
            print(frameScore)
            numObject += 1

            (x, y, w, h) = cv2.boundingRect(c)
            #cv2.rectangle(frame, (x, y), (x + w, y + h), (0, 255, 0), 2)
            cv2.drawContours(frame, [c], -1, (36,255,12), 2)

    if (numObject != 0):
        frameScore /= numObject
    
    sizeScore += frameScore

    #firstFrame = frame

    cv2.imshow('Frame', frame)
    cv2.imshow('FG MASK Frame', thresh)
    cv2.imshow('FG MASK delta', frameDelta)


    keyboard = cv2.waitKey(20)
    if keyboard == ord("q"):
        break


frames = cap.get(cv2.CAP_PROP_FRAME_COUNT) 
fps = int(cap.get(cv2.CAP_PROP_FPS)) 

sizeScore /= frames
sizeScore = utils.sizeScore(sizeScore)
recordLength = int(frames / fps) 

score = utils.severityCalculation(timeInRange, recordLength, sizeScore)
print(sizeScore)
print(recordLength)
print(score)

cap.release()
cv2.destroyAllWindows()