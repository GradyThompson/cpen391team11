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


fgbg = cv2.createBackgroundSubtractorMOG2()
while True:
    ret, frame = cap.read()
    if frame is None:
        break
    
    fgmask = fgbg.apply(frame)
    count += 1
    frameScore = 0
    numObject = 0

    (contours, hierarchy) = cv2.findContours(fgmask.copy(), cv2.RETR_EXTERNAL,cv2.CHAIN_APPROX_SIMPLE)
    for c in contours:
            if cv2.contourArea(c) < 500:
                continue

            frameScore += cv2.contourArea(c)
            print(frameScore)
            numObject += 1

            (x, y, w, h) = cv2.boundingRect(c)
            cv2.rectangle(frame, (x, y), (x + w, y + h), (0, 255, 0), 2)

    if (numObject != 0):
        frameScore /= numObject
    
    sizeScore += frameScore

    cv2.imshow('Frame', frame)
    cv2.imshow('FG MASK Frame', fgmask)

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