#! /usr/bin/env python3

import csv
import sys
import matplotlib.pyplot as plt


def main():
    """
    take a .txt file containing coordinates x, y
    and plot the points on a figure the x must be
    sorted
    """

    COLORS = ['r', 'g--', 'b-.', 'c:', 'm']
    ci = 0

    for arg in sys.argv[1:]:
        x, y = [], []
        print(arg)
        with open(arg, 'r') as f:
            reader = csv.reader(f, delimiter=' ')
            for row in reader:
                x.append(float(row[0]))
                y.append(float(row[1]))
        plt.plot(x, y, COLORS[ci], label=arg[:-4])
        ci += 1
    plt.legend()
    plt.show()

if __name__ == "__main__":
    main()
