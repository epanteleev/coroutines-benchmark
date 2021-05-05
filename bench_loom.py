import util
import math

NUM_TESTS = 10


def startBench(coroutines) -> int:
    return int(util.run_loom('loom/Bench0.java', coroutines)[2])

print('--Start loom bench--')

time = [startBench('100000') for i in range(NUM_TESTS)]

mean, std = util.stdev(time)

print('Result: {} +/- {}'.format(mean, std))
