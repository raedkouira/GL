#! /usr/bin/env python3

import os
import sys
import numpy as np

T_MATH = 432

def createAndCompile(filename, func, number):
    f = open(filename, 'w')
    f.write('#include "Math.decah"\n\n')
    f.write('{\n')
    f.write('\tMath m = new Math();\n')
    f.write('\tm.{}({});\n'.format(func, number))
    f.write('}\n')
    f.close()
    os.system("decac {}".format(filename))
    delete(filename)

def delete(filename):
    os.system("rm {}".format(filename))

def main():
    for n in [float(f) for f in np.linspace(-100, 100, 200)]:
        createAndCompile("tmp.deca", sys.argv[1], n)
        os.system('echo "d" | ima -d tmp.ass | grep "Temps d\'execution :" | cut -c63- > res.txt')
        delete("tmp.ass")
        resF = open("res.txt", 'r')
        res = int(resF.read())
        resF.close()
        res -= T_MATH
        delete("res.txt")
        print(n, res)


main()
