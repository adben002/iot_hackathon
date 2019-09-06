This folder contains the on device application for collecting and sending data.

####Setting up virtualenv
First have an installation of python2.7 and pip:

Install virtualenv:
```sh
$ sudo pip install virtualenv
```

To setup the virtual environment run the script:
```sh
$ . start_env.sh
```

If new requirements have been added run:
```sh
$ pip freeze > requirements.txt
```

To leave the virtualenv run:
```sh
$ deactivate
```
