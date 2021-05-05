import subprocess
import math

GO_HOME = 'D:\Go'
LOOM_HOME = 'D:\Java\jdk-17'
MAVEN_HOME = 'D:\Java\apache-maven-3.6.3'


def stdev(data):
    def mean(data):
        return sum(data) / len(data)


    def variance(m, data):
        deviations = [(x - m) ** 2 for x in data]
        return mean(deviations)


    m = mean(data)
    var = variance(m, data)
    std_dev = math.sqrt(var)
    return m, std_dev


def start_proc(args_list: list):
    return subprocess.check_output(args_list).split()


def run_loom(class_name, arg):
    return start_proc([LOOM_HOME + '/bin/java', '-XX:CompileThreshold=100', class_name, arg])


def run_go(file_name, arg):
    return start_proc([GO_HOME + '/bin/go', 'run', file_name, arg])


def build_go(file_name):
    return start_proc([GO_HOME + '/bin/go', 'build', file_name])
