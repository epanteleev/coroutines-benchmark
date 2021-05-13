import util
import math

NUM_TESTS = 10


def startBench(coroutines) -> int:
    return int(util.run('bench0', [coroutines])[2])


print('--Start go bench--')
util.build_go('go/bench0.go')

time = [startBench('100000') for i in range(NUM_TESTS)]

mean, std = util.stdev(time)

print('Result: {} +/- {}'.format(mean, std))

util.remove('bench0')
