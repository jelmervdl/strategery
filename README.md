# What is this?
A simple game which looks a bit like a simplified version of Risk. It was implemented as the environment for building a player that learns through reinforcement learning. This project is an assignment for the Machine Learning course 2012.

# Compiling
Run `ant compile` in the root directory. This should build your classes in build/classes.

# Usage
The first argument is the default configuration. All the following argumens are deviations of this default configuration which will be executed as experiments. Call `java -cp build/classes TestGame default.conf ./experiments/my-experiment/*.conf` from the root directory. The results of the experiments will have the same names as their configuration files but with `.csv` appended.

All experiments are run concurrently in their own thread. There is currently no feedback on their status or progress.