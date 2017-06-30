# HomomorphicEncryption

# **HomE**

HomE is an ```incomplete``` **Hom**omorphic **E**ncryption class that is using Paillier and ElGamal encryption algorithms.
Only **add** opearation satisfies homomorphic property while other operations (subtract,multiply,divide) requires *decryption*.

## Getting Started

These instructions will get you a copy of the project up and running on your local machine
for development and testing purposes. See deployment for notes on how to deploy the project on a live system.

### Prerequisites

Project requires no specific prerequisite. You will be okey if you have a standart working Java Development Environment with JavaSE - 1.8

### Installing

After you get all the files in your development environment you're almost done!

#### Summary of Data Types
Plain data type : double
Encrypted data type : List<BigInteger>
Operation methods' return type : List<BigInteger>
Encrypt method's return type : List<BigInteger>
Decrypt method's return ryoe : double


Example cases are provided below


Initialise your HomE class and set the encryption algorithm you want to use

```

String algorithm = "paillier";
Homomorphic he = new Homomorphic(algorithm);
```

Set variables as `double`

```
double v1 = 14, v2 = 13;
```

Encrypt those variables

```
List<BigInteger> v1_e = he.encrypt(v1);
List<BigInteger> v2_e = he.encrypt(v2);
```

Perform calculations. If you want to dump give third paramater ```true```

```

List<BigInteger> addResultEncrypted = he.add(v1_e, v2_e,true); // If you don't want dump use with no third paramter, he.add(v1_e, v2_e)
List<BigInteger> multiplyResultEncrypted = he.multiply(v1_e, v2_e,true);
List<BigInteger> subtractResultEncrypted = he.subtract(v1_e, v2_e,true);
List<BigInteger> divideResultEncrypted = he.divide(v1_e, v2_e,true);
```

That's all in fact, keep your encrypted data on your cloud and perform calculations on those encrypted data.

**REMIND:** Only *add* operation satisfies homomorphic property. Other operations basically derived from homomorphic *add* operation and requires *decryption* before calculations.

## Built With

* JavaSE 1.8

## Authors

* **Batuhan Kayım** - *inital work* - [git(bakayim)](https://github.com/Bakayim)


## License

This project is licensed under the MIT License.

## Acknowledgments

* ElGamal and Paillier encryption algoritms are not implemented by me. They're collected from different sources on internet. Thanks to their developers for implementation efforts.
