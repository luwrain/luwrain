
import svgwrite
from math import sqrt

bottom = 300
bottom_w=70
center = 250
root=sqrt(2)
space=40

l_left_len = 320
l_right_len = 350 / 2
l_width = 50

w_len=100
w_width = 40

l_left1 = (round(center - l_left_len / root), round(bottom - l_left_len / root))
l_left2 = (round(center - l_left_len / root + l_width / root), round(bottom - l_left_len / root - l_width / root))
l_center1 = (center, bottom)
l_center2 = (center, round(bottom - l_width * root))
l_right1 = (round(center + l_right_len / root), round(bottom - l_right_len / root))
l_right2 = (round(center + l_right_len / root - l_width / root), round(bottom - l_right_len / root - l_width / root))

w_left1 = (round(center - w_len / root), round(bottom_w + w_len / root))
w_left2 = (round(center - w_len / root), round(bottom_w + w_len / root - w_width * root))

w_left1a = (round(w_left1[0] - w_len / root), round(w_left1[1] - w_len / root))
w_left2a = (round(w_left1a[0] + w_width / root), round(w_left1a[1] - w_width / root))

w_right1 = (round(center + w_len / root), round(bottom_w + w_len / root))
w_right2 = (round(center + w_len / root), round(bottom_w + w_len / root - w_width * root))

w_right1a = (round(w_right1[0] + w_len / root), round(w_right1[1] - w_len / root))
w_right2a = (round(w_right1a[0] - w_width / root), round(w_right1a[1] - w_width / root))




w_center1 = (center, bottom_w)
w_center2 = (center, round(bottom_w - w_width * root))

center1 = (center, l_center2[1] - space)
center3 = (center, w_center1[1] + space)
center2 = (center - (center1[1] - center3[1]) / 2, center3[1] + (center1[1] - center3[1]) / 2)
center4 = (center + (center1[1] - center3[1]) / 2, center3[1] + (center1[1] - center3[1]) / 2)


print(w_center2[1])


lcolor = svgwrite.rgb(10, 10, 16, '%')

dwg = svgwrite.Drawing('luwrain.svg')
dwg.add(dwg.polygon(points=[l_left1, l_left2, l_center2, l_right2, l_right1, l_center1], style="fill: blue;"))
dwg.add(dwg.polygon(points=[w_left1, w_left1a, w_left2a, w_left2, w_center2, w_right2, w_right2a, w_right1a, w_right1,w_center1], style="fill: blue;"))
dwg.add(dwg.polygon(points=[center1, center2, center3, center4], style="fill: blue;"))

dwg.save()
