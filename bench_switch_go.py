import util
import math

NUM_TESTS = 10


def startBench(coroutines) -> int:
    return int(util.run('benchSwitch', [coroutines])[7])


print('--Start go bench--')
util.build_go('go/benchSwitch.go')

switchesPerSecond = [startBench('100') for i in range(NUM_TESTS)]

mean, std = util.stdev(switchesPerSecond)

print('Result: {} +/- {}'.format(mean, std))

util.remove('benchSwitch')
