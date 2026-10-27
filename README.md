# Практическая работа 7

Корпоративный заказ оборудования.

Сборка и запуск проверок без Maven:

```bash
mkdir -p out
javac -encoding UTF-8 -d out $(find src -name "*.java")
java -cp out ru.edu.pr04.SelfCheck
```

Запуск приложения на файле данных:

```bash
java -cp out ru.edu.pr04.Main data/sample.csv
```

Скрипты `run_selfcheck.sh` и `run_selfcheck.bat` выполняют сборку и SelfCheck одной командой.
