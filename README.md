# fncli
Forward Networks CLI Demo

This is a small demostration of leveraging the power of universal models and network search to simplify day to day operations tasks.

## Build

1. Install shadow-cljs `npm install shadow-cljs -g`
2. Clone repository `git clone https://github.com/gaberger/fncli.git`
3. Download dependencies `npm install`
4. Create bin directory `mkdir bin`
4. Build native-binary `shadow-cljs clj-run fncli.build/native`

## Binary Install
1. Download release for your OS https://github.com/gaberger/fncli/releases
2. Run `fncli-<osdep>`


## Dependencies
1. Must have an account on the demo server
2. Must set the environmental variables
    - FN_USER= `<username>`
    - FN_PASSWORD= `<password>`
    - FN_SNAPSHOT= `<snapshot-id>`

## Commandline

Linux
```
$ fncli-linux
Usage: fncli [options] [command]

Forward Networks CLI

Options:
  -V, --version  output the version number
  -D --debug     Debug
  -J --json      Output to JSON
  -E --edn       Output to EDN
  -W --wide      Wide screen output
  -h, --help     output usage information

Commands:
  show <object>  Show operations
  help [cmd]     display help for [cmd]
  ```

## SubCommands
```$ fncli-linux show interfaces```

![show-interfaces](https://github.com/gaberger/fncli/blob/master/images/show-interfaces.png)


```$ fncli-linux show config atl-ce01```

![show-config](https://github.com/gaberger/fncli/blob/master/images/show-config.png)

