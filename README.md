
FavDownloader
========================================================
お気に入りツイートのダウンローダーです。

- お気に入りに追加したツイートのメディアエンティティ（画像など）を一括ダウンロードします。
- ダウンロード先に指定したフォルダーに、ScreenName別ごとに分けて保存します。
- ファイル名の先頭にツイートのIDを追加しているため、元ツイートを探せます。
- 自分のアカウントから参照可能な、他ユーザーの一覧を表示しダウンロードもできます。

[MITライセンス](http://ja.wikipedia.org/wiki/MIT_License)なのをご理解のうえご利用ください。

なにかあれば、[Twitter(@alalwww)](https://twitter.com/alalwww)か、[Issues](https://github.com/alalwww/FavDownloader/issues)へ。


ScreenShots
-----------------------
<img src="../ss/1.png" height="150" alt="twitter oauth" title="Twitter認証">
<img src="../ss/2.png" height="150" alt="twitter favs list" title="一覧画面">
<img src="../ss/3.png" height="150" alt="download progress" title="ダウンロード中">

Requirements
-----------------------
- Java 8 or upper
    - ※ Windows用のランタイム(JRE)付属版を使用する場合はJava8のインストールは必須ではありません。

Downloads and install
-----------------------
以下よりダウンロードできます。
- [Releases](https://github.com/alalwww/FavDownloader/releases/latest)

### Windows用(JREあり/JREなし)
インストーラー等は付属していませんが、特別なインストール手順などもありません。

ダウンロードしたZIPファイルを、任意の「書き込み権限のあるフォルダー」以下に、フォルダー構成そのままで展開し、FavDownloader.exe を実行してください。


JREあり版はファイルサイズが大きいですが、Java8をインストールする必要なく動作します。

JREなし版を利用する場合は、実行するパソコンにJava8がインストールされている必要があります。

### JAR版
Java8をインストールした上で、各OSのJavaコマンドの使用方法にそってJarファイルを実行して下さい。

Windowsであれば、ダウンロードしたJARファイルを任意のフォルダーに配置しダブルクリックするか、
`/path/to/java -jar FavDownloader-x.x.x-standalone.jar`コマンドで起動できるかと思います。


使い方など
-----------------------

現時点では設定ファイルなどはなく、状態の保存や取得したツイートや画像ファイルの恒久的なキャッシュは行っていません。
ログファイルのみ出力します。

Twitterの認証情報も保持しておらず、起動時に毎回Twitter認証を行います。
認証時に要求する権限はTLなどの読み取りのみで、書き込みやDM閲覧権限は要求しません。

Twitter認証情報の受け取りに、一時的にWebサーバーとして機能します。
そのため、Javaがポート監視の許可を求めてくる場合があります。
許可しないとこのソフトウェアは使用できません。(PIN認証の機能はありません)

一度に取得可能なお気に入りツイートはTwitterAPIの取得上限である200件です。
また、同APIの制限により、最初に取得してから15分間に15回までしか取得できません。
15分経過するとAPI残数はリセットされ、次に取得してから15分間で15回まで取得できるようになります。

更新ボタンを押すと、取得済みのツイートを全て破棄し、最新のお気に入り200件を再取得します。

~~ツイートの一覧を一番下までスクロールすると、現在取得しているお気に入りよりも古いお気に入りを追加で取得します。~~ (v0.0.1時点では未実装のため最新200件しか取れない)

画像は表示時に取得しているため、画像のロードなどで一覧のスクロールが固まる事がありますが、現時点では仕様です。
回線速度や通信状態に依存します。
取得した画像は取得順に500件程度はキャッシュしていますが、一覧のツイート総数が多いと、キャッシュが足りずにスクロールが重くなることもあります。
圧縮もリサイズもせずメモリにぶち込んでるような感じなので、環境によってはメモリ不足エラーで落ちるかもしれません。(起きたら教えてください…)

ダウンロード時はキャッシュに関係なく、全て再取得し直します。


-----------------------


For developer
-----------------------

1. If necessary, install javafx-gradle to local maven repositories.

    1. clone from https://bitbucket.org/shemnon/javafx-gradle/overview and checkout any version 8.1.1's revision

    2. install
        ```
        > gradlew install
        ```

2. clone FavDownloader repository.

3. if necessary, create your `gradle.properties` file in your project directory.
    - example)

        ```properties:gradle.properties
        org.gradle.java.home=C:/Program Files (x86)/Java/jdk1.8.0_25
        jfxrtDir=C:/Program Files (x86)/Java/jdk1.8.0_25
        ```

### build and run

```
> gradlew build
> java -jar build/libs/favdler-core-x.x.x.jar
```

### generate eclipse project settings
```
> gradlew eclipse
```

#### using eclipse, but e(fx)clipse plugin not installed
- Add these property keys and empty values to `gradle.properties` file.

```properties:gradle.properties
xtext_nature=
xtext_builder=
eFXclipse_container_path=
```


License
-----------------------
- This software is available under the [MIT License](http://opensource.org/licenses/mit-license.php).
- Unless otherwise specified, other content (ex: image file) is under [CC BY 4.0](https://creativecommons.org/licenses/by/4.0).


Credit
-----------------------
- [CREDITS](https://github.com/alalwww/FavDownloader/blob/master/CREDITS.md)


---------------------


### ( っ'ω'c)

- デフォルトではeclipseタスクはe(fx)clipseプラグインのインストールされている環境を想定したファイルを生成します。
それらが必要ない場合、gradle.propertiesを用いて追加されないようにできます。(上記)

- 32bitOS用にネイティブパッケージングされたファイルの出力をするのであれば、32bitのJDK8が必要です。
64bitのJDKを用いた場合、ネイティブパッケージングされたファイルは32bit環境では動作しません。

- `gradle.properties`ファイルを作らない場合、javafx-gradleプラグインがシステムデフォルトから適当に解決してくれます。
が、期待通りになるかはわかりません。JDKのパスなどは明示したほうが安全です。

- javafx-gradleプラグインはローカルリポジトリへのインストールが推奨されているようですが、必須ではありません。
ネットワーク環境が常に使えるのであればそのままでも使えます。（多分）

- その他、FXまわりのGradleタスクの詳細は[javafx-gradle](https://bitbucket.org/shemnon/javafx-gradle)プラグインを参照してください。

- ネイティブ・パッケージングされたexeファイルは、`build/distributions/FavDownloader/`に出力されます。
リリース用のファイル群は`build/distributions/`以下に出力されます。

- 習作ツールなのであちこち手抜きだったり無駄に冗長コードだったりしますが、仕様です。
いつか再利用したいなーとか思いつつ余計なコード書きすぎた。殆どテストは書いてないのに…。

