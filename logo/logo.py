
import svgwrite
from math import sqrt
bottom = 320
center = 250
root=sqrt(2)

l_left_len = 350
l_width = 100

l_left1 = (round(center - l_left_len / root), round(bottom - l_left_len / root))
l_left2 = (round(center - l_left_len / root + l_width / root), round(bottom - l_left_len / root - l_width / root))

l_center1 = (center, bottom)
l_center2 = (center, round(bottom - l_width * root))


print(l_left1)
print(l_left2)

print(l_center1)
print(l_center2)

lcolor = svgwrite.rgb(10, 10, 16, '%')

dwg = svgwrite.Drawing('luwrain.svg')
dwg.add(dwg.polygon(points=[l_left1, l_left2, l_center2, l_center1], style="fill: blue;"))

print(dwg.tostring())
dwg.save()
