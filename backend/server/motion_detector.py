import sys
import numpy as np
import cv2
from datetime import *
import utils
import math
import json

cap = cv2.VideoCapture(sys.argv[1])
#cap = cv2.VideoCapture('server/vtest.avi')
ret, first = cap.read()

date = sys.argv[2]
hh = date[11:13] if date[11] != '0' else date[12:13]
mm = date[14:16] if date[14] != '0' else date[15:16]

first = cv2.resize(first, (500, 500))
first_gray = cv2.cvtColor(first, cv2.COLOR_BGR2GRAY)
first_gray = cv2.GaussianBlur(first_gray, (21, 21), 0)

recordTime = time(hour = int(hh), minute = int(mm))

sizeScore = 0
mvScore = 0
count = 0
cList = []
newList =[]
initList = False

while True:
    ret, frame = cap.read()

    if frame is None:
        break

    frame = cv2.resize(frame, (500, 500))
    gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
    gray = cv2.GaussianBlur(gray, (21, 21), 0)

    # compute the absolute difference between the current frame and
	# first frame
    frameDelta = cv2.subtract(first_gray, gray)
    thresh = cv2.threshold(frameDelta, 25, 255, cv2.THRESH_BINARY)[1]
	# dilate the thresholded image to fill in holes, then find contours
	# on thresholded image
    thresh = cv2.dilate(thresh, None, iterations=2)

    count += 1
    frameScore = 0
    frameMvScore = 0
    numObject = 0


    (contours, hierarchy) = cv2.findContours(thresh.copy(), cv2.RETR_EXTERNAL,cv2.CHAIN_APPROX_SIMPLE)
    for c in contours:
            if cv2.contourArea(c) < 500:
                continue

            frameScore += cv2.contourArea(c)
            #print(frameScore)
            numObject += 1

            (x, y, w, h) = cv2.boundingRect(c)
            cv2.drawContours(frame, [c], -1, (36,255,12), 2)

            if not initList:
                cX = (x + w) / 2
                cY = (y + h) / 2
                cList.append((cX, cY, False))

    if initList:            
        for c in contours:
            index = -1
            matchFound = False
            min_dist = 500
            (x, y, w, h) = cv2.boundingRect(c)
            cX = (x + w) / 2
            cY = (y + h) / 2

            for i in range(len(cList)):
                old_x, old_y, updated = cList[i]
                if not updated:
                    euc_dist = math.sqrt(float((cX - old_x)**2) + float((cY - old_y)**2))
                    #print(euc_dist)
                    if euc_dist < min_dist:
                        index = i
                        min_dist = min(min_dist, euc_dist)
                        matchFound = True
            
            if matchFound:
                frameMvScore += min_dist
                cList[index] = (cList[index][0], cList[index][1], True)
            else:
                newList.append((cX, cY, False))
        
        toPop =[]
        for i in range(len(cList)):
            if not cList[i][2]:
                toPop.append(i)

        for i in range(len(toPop) -1, 0, -1): 
            cList.pop(toPop[i])        
        cList.extend(newList)
    
    initList = True

    if (numObject != 0):
        frameScore /= numObject
        frameMvScore /= numObject
    
    sizeScore += frameScore
    mvScore += frameMvScore

    # cv2.imshow('Frame', frame)
    # cv2.imshow('FG MASK Frame', thresh)
    # cv2.imshow('FG MASK delta', frameDelta)

    keyboard = cv2.waitKey(20)
    if keyboard == ord("q"):
        break


frames = cap.get(cv2.CAP_PROP_FRAME_COUNT) 
fps = int(cap.get(cv2.CAP_PROP_FPS)) 

sizeScore /= frames
mvScore /= frames
#print(frameMvScore)
timescore = utils.checkTime(recordTime)
sizeScore = utils.scaleSizeScore(sizeScore)
mvScore = utils.scaleMVScore(mvScore)
recordLength = int(frames / fps) / 60

# print(timescore)
# print(sizeScore)
# print(mvScore)
# print(recordLength)

score = utils.severityCalculation(recordTime, recordLength, sizeScore, mvScore)
print(int(round(score / 10)))
sys.stdout.flush()

cap.release()
# cv2.destroyAllWindows()