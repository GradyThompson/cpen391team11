from datetime import *

'''
1. Time when the video was taken     : 30
2. Size of the object                : 30
3. Amount of movement of the object  : 20
4. Length of the video               : 20
'''

def checkTime(recordTime):

	start = time(hour = 22, minute = 0)
	end = time(hour = 6, minute = 0)

	if (start <= recordTime or recordTime <= end):
		return 30
	else:
		recordStart = recordTime.hour * 60 + recordTime.minute
		diff = min(abs(recordStart - start.hour * 60), abs(recordStart - end.hour * 60))
		return 30 - round(diff/60) * 2

def scaleSizeScore(score):
	res = score / 500
	res = 20 if (res > 20) else res
	return res

def scaleMVScore(score):
	res = score / 200
	res = 20 if (res > 20) else res
	return res

def severityCalculation(recordTime, recordLength, size, mv):
	score = 0

	score += checkTime(recordTime)

	if (recordLength > 20):
		score += 20
	else:
		score += recordLength

	score += size
	score += mv

	return score

	
	