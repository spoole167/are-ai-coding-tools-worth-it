This module contains the results of asking a set of AIs the following 
---

Your mission, should you decide to accept it, is deceptively simple: write a Java program for retrieving temperature measurement values from a text file and calculating the min, mean, and max temperature per weather station. There’s just one caveat: the file has 1,000,000,000 rows!

The text file has a simple structure with one measurement value per row:
Hamburg;12.0
Bulawayo;8.9
Palembang;38.8
St. John's;15.2
Cracow;12.6
…
Note that records that begin with a hash (‘#’) are to treated as comments and ignored.

The program should print out the min, mean, and max values per station, alphabetically ordered like so:
{Abha=5.0/18.0/27.4, Abidjan=15.7/26.0/34.1, Abéché=12.1/29.4/35.6, Accra=14.7/26.4/33.1, Addis Ababa=2.1/16.0/24.3, Adelaide=4.1/17.3/29.7, ...}

The goal of the 1BRC challenge is to create the fastest implementation for this task, and while doing so, explore the benefits of modern Java and find out how far you can push this platform. So grab all your (virtual) threads, reach out to the Vector API and SIMD, optimize your GC, leverage AOT compilation, or pull any other trick you can think of.

There’s a few simple rules of engagement for 1BRC 
Any submission must be written in Java
No external dependencies may be used
There must be a comment to identify the version of Java being used.
The input file name will be provided as the first argument on the command line
Make sure all Java code includes all necessary import statements.
Give the class a one word package name based on  your name
Include Javadoc and other comments to explain what the program does
