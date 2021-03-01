from datetime import *

def checkTime(userStart, userEnd, recordTime):

	if (userStart is None or userEnd is None):
		userStart = time(hour=22, minute=0)
		userEnd = time(hour=6, minute=0)

	crossMidnight = True if (userStart > userEnd) else False

	if (crossMidnight):
		if (userStart < recordTime or recordTime < userEnd):
			return True
		else:
			return False
	else:
		if (userStart < recordTime and recordTime < userEnd):
			return True
		else:
			return False

def sizeScore(size):
	res = size / 200
	res = 20 if (res > 20) else res
	return res

def severityCalculation(timeInRange, recordLength, size):
	score = 0
	if (timeInRange):
		score += 50

	if (recordLength > 20):
		score += 20
	else:
		score += recordLength

	score += size

	return score

	
	