#! /usr/bin/env python3
import pandas as pd
df = pd.read_csv("data.csv", header=None);
gt = (df[0] >= 500)
lt = (df[0] <= 600)

df.loc[gt & lt,:].to_csv("expected/between.csv", header=None, index=False);
df.loc[lt,:].to_csv("expected/lt.csv", header=None, index=False);
df.loc[gt,:].to_csv("expected/gt.csv", header=None, index=False);

b = (df[1] >= 100) & (df[1] <= 200)
df.loc[b & gt & lt,:].to_csv("expected/composite.csv", header=None, index=False);

