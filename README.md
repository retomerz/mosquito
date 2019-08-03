# A experimental project 

Basically the idea is to visually detect a mosquito, focus and track it with a laser until it dies.

## Some facts
Only female mosquitoes imbibe blood and females are bigger than males [1][2].
The buzzing sound of a mosquito has a unique characteristic.
Flight speed is around 1.5-2.5 km/h [2] (or 1-2 km/h [1]).
The typical length of a mosquito is 3-6mm [2].

1 km/h ~ 0.28 m/s -> 2.7mm per 10ms<br/>
2.5 km/h ~ 0.7 m/s -> 7mm per 10ms

=> The overall target latency of the tracking mechanism should be 10ms or less.

[1] https://en.wikipedia.org/wiki/Mosquito<br/>
[2] https://de.wikipedia.org/wiki/Stechm%C3%BCcken

## Second prototype

It turned out that [Tangmi 1080P](https://www.amazon.de/dp/B01J0RC5Z6/ref=pe_3044161_185740101_TE_item) and [HP 4310](https://www.conrad.ch/de/full-hd-webcam-1920-x-1080-pixel-hp-4310-klemm-halterung-standfuss-1555220.html) is much too slow so I get a [IDS UI-3160CP-C-HQ](https://en.ids-imaging.com/store/ui-3160cp-rev-2-1.html) with a [SV-0614H](http://www.rmaelectronics.com/vs-technology-sv-0614h-2-3-6mm-f1-4-manual-iris-c-mount-lens-5-megapixel-rated/) objective.

![v2](https://github.com/retomerz/mosquito/raw/master/docs/v2.jpg)

Main components
- [TF Servo Brick](https://www.tinkerforge.com/en/doc/Hardware/Bricks/Servo_Brick.html)
- [TF Dual Relay Bricklet](https://www.tinkerforge.com/en/shop/bricklets/io/dual-relay-bricklet.html)
- [Servo Hitec HS-53](https://www.tinkerforge.com/en/shop/accessories/motors/servo-hitec-hs-53.html)
- [Red Dot Laser 1mW 650nm](https://www.conrad.ch/de/lasermodul-punkt-rot-1-mw-laserfuchs-lfd650-1-129x20-816476.html)
- [IDS UI-3160CP-C-HQ](https://en.ids-imaging.com/store/ui-3160cp-rev-2-1.html)
- [SV-0614H](http://www.rmaelectronics.com/vs-technology-sv-0614h-2-3-6mm-f1-4-manual-iris-c-mount-lens-5-megapixel-rated/)

## First prototype
![v1](https://github.com/retomerz/mosquito/raw/master/docs/v1.jpg)

Main components
- [Mindstorms EV3](https://www.amazon.de/dp/B00BMKLVJ6/ref=pe_3044161_189395811_TE_dp_1)
- [Red Dot Laser 1mW 650nm](https://www.conrad.de/de/lasermodul-punkt-rot-1-mw-laserfuchs-lfd650-112x45-nt-817808.html)
- [Tangmi 1080P](https://www.amazon.de/dp/B01J0RC5Z6/ref=pe_3044161_185740101_TE_item)

## Links to interesting hardware
- https://optitrack.com/products/prime-41/
- https://www.robosense.ai/seeker
- https://www.flir.de/
- https://www.faro.com
- https://www.gras.dk/products/special-microphone/low-noise-measuring-systems

## Links and similar projects
- [Nathan Myhrvold - Could this laser zap malaria? - February 2010 at TED2010](https://www.ted.com/talks/nathan_myhrvold_could_this_laser_zap_malaria?language=en)
- [Photonic Sentry](https://photonicsentry.com/)
- [Mosquito Killer Robot](http://en.leishen-lidar.com/product/miewenpao/index.html)

## Credit
Icons: https://www.behance.net/gallery/24974365/Thin-Line-UI-Icons-170
