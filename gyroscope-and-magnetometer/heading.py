from math import atan,pi   # Return the arc tangent of x, in radians.

def heading(x: float, y: float) -> float:
    '''
    compute magnet heading
    '''
    x = float(x)
    y = float(y)
    heading = 0
    if x > 0:
        heading = 270 + atan(y / x) * 180 / pi
    elif x < 0:
        heading = 90 + atan(y / x) * 180 / pi
    elif x == 0:
        if y >= 0:
            heading = 0
        elif y < 0:
            heading = 180
    return heading

if __name__ == "__main__":
    x = input("input x:")
    y = input("input y:")
    print("magnet heading is:",heading(x, y))
