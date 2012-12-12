# What is this?
A simple game which looks a bit like a simplified version of Risk. It was implemented as the environment for building a player that learns through reinforcement learning. This project is an assignment for the Machine Learning course 2012.

# Compiling
Run `ant compile` in the root directory. This should build your classes in build/classes.

If things behave weird, or methods cannot be found, try recompiling by first running `ant clean` and then again `ant compile`.

# Running Experiments
Create a configuration file for each configuration you want to test. For example, if I want to test a different number of hidden units, create two files:

nn-1.conf:
```
[network]
hidden_units  i 	1
```

nn-10.conf:
```
[network]
hidden_units  i   10
```

Now I can run these two experiments by calling:
```
java -cp build/classes TestGame ./default.conf ./nn-*.conf
```

Note that I first pass default.conf as this contains all the other configuration options. Only the parameters named in the individual experiment configurations will be altered for that particular experiment.

After all the experiments have finished, a summary will be printed. Also, for each run of each experiment a file named `config.conf-run-#.csv` will be created, in which config.conf is replaced with the path of the configuration file for that experiment, and # is the number of the run.
