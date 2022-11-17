# Online Car Marketplace

This project utilizes multiple classes, packages, and methods to create a terminal based car marketplace.

Made by CS 180 Lab L05 Group 3 Brennan Johnson, Adrian Mao, Alvin Lee, Raunak Chakrabarty, and Vinathi Muthyala

## Features

- Users are prompted to login or make an account, either as a customer or a seller.
- Customers are able to view cars on the market, search for specific ones, Sort the options by price and/or quantity, purchase vehicles, view purchase history, export purchases, and manage their shopping cart.
- Sellers are able to create/edit/delete their own cars on the market, view their sales statistics, view shopping carts that contain their products, and import/export product info.
- All the information of the marketplace and the transaction history is located on specified file thart are easily accessable.
- Multiple Stores are in the database with different products in each one.
- The program uses an organized menu function with clear instructions for each of the listed features.


## Installation

Copy the classes into an IDE of your choosing. After compiling the classes, run the Market class to run the program.

## Market.java

The Market class is the class that contains the main method to the marketplace and runs the program. This is the class to be ran in the terminal to run the program. Some methods pertaining to file I/O not specific to any of the other classes are located here as well. The market class calls a lot of methods written in other classes as well as distributes the processing for said methods elsewhere to keep the main one clean.

Testing Info: Listings.txt was present/not present, making sure Listings.txt would have the correct info, making sure Listings.txt would update every time the program closed, etc... Also tested inputs in the login sections: whether people could have blank usernames, and whether incorrect inputs could be handled well.

## User.java

The User class is the class used to manage the account login/registration for users of the marketplace. It contains multiple methods to manage account info and how to get the user from the login page at startup of the program to the actual marketplace. This class is the superclass of Sellers.java and Buyers.java.

Testing Info: Non integer input for first choice, non 1 or 2 input for first choice, special characters in username/password, no users in UserAccounts.txt, UserAccounts.txt not created prior to running program, different username same password, non "buyer" or "seller" input for buyer or seller input. Also tested usernames, whether they had commas(which would mess up our delimiters), or were empty.

## Buyers.java

The Buyers class is the class that contains all the methods and processing related to a customer using the program. It includes the methods that add to the files, make the purchases, shopping cart, and manages control flow of the marketplace when a customer is logged in using the program. This class is a subclass of the User class.

Testing Info: Non-integer inputs for choice prompt, invalid/negative integer inputs for first prompt. In the first five prompts, tested with and without the prescence of Listings.txt. When checking out and editing carts, tested with and without anything in cart. When viewing statistics or viewing purchase history, tested with and without any purchase history. 
Tested incorrect inputs for viewing listings. Tested searching for products when there was nothing, or when Listings.txt is gone. Tested error input handling when incorrect input was put for sorting. When purchasing, tested with nothing in Listings, or when no name was found for the file. When viewing purchase history, tested with and without anything bought.
When checking out, tested with and without anything in cart, and with/without a cart file existing. Same for testing editing carts. When exporting, tested with and without any purchase history, as well as with/without the purchase history file present. When viewing statistics, tested when lots or no products have been sold, and tested incorrect input for sort mechanism.

## Sellers.java

The Sellers class is the class that contains all the methods and processing related to a seller using the program. It includes the methods that add to the product and store files, product management, store management, and managing control flow of the marketplace when a seller is logged in using the program. This class is a subclass of the User class.

Testing Info: Non-integer inputs for choice prompt, invalid/negative integer inputs for first prompt. When creating products, tested with non-integer/double inputs for the prompted integers/doubles, as well as blank names/descriptions. When editing products, ensured the product number given to edit is small enough, and tested the same things as creating a new product. 
When deleting a product, ensured number entered was within the amount of products, and ensured a number was entered. When seeing products, tested with and without any products created, and tested with and without any revenue made. When viewing carts, tested with and without any carts available, and with/without anything in carts. When importing and exporting, tested when 
importing file didn't exist, importing file was in the incorrect format, and when nothing was sold when exporting. Every time an integer was prompted to be entered(such as within editing/deleting products), ensured program handled cases correctly. When specific input was asked for(such as asking for y/n when confirming to sort), input was also tested.

## Product.java

The Product class is the class that contains all the methods and processing related to the product objects used in the marketplace. Multiple methods used to manage a product's information are stored in here. 
Testing info: Tested having blank/incorrect names, tested equals method, directly tested product pages and listings.

## Store.java

The Store class is the class that contains the methods for managing a store object that contains products in the marketplace. The class also has methods that manage some file I/O specific to store information. 
Main testing was in printing the store to a file: ensuring input/output was correct, etc... Also made sure a seller would link up with a correct store.

## Submissions

Submitted report to Brightspace: Brennan Johnson

Submitted Vocareum Workspace: Adrian Mao
