package com.example.testecomposesql

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Bundle
import android.provider.BaseColumns
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dataSource = MovieDataSource(this)
        // Adiciona um log para mostrar o diretório do banco de dados
        val databasePath = dataSource.getDatabasePath()
//        Log.d("Database Path", databasePath.toString())
        dataSource.deleteAllMovies()
        dataSource.populateMovies()
        setContent {
            val navController = rememberNavController()
            val movies = dataSource.getAllMovies()
            NavHost(navController, startDestination = "MyScreen6") {
                composable("MyScreen6") { MyScreen6(navController, movies) }
                composable("DetailScreen/{title}") { backStackEntry ->
                    val title = backStackEntry.arguments?.getString("title")
                    val movie = findMovieByTitle(title, movies)
                    if (movie != null) {
                        DetailScreen(navController, movie)
                    } else {
                        Text(text = "Movie not Found")
                    }
                }
            }
        }
    }
}

@Composable
fun MyScreen6(navController: NavController, movies: List<Movie>) {
    val groupedByGroup = movies.groupBy { it.grupo }

    LazyColumn {
        groupedByGroup.forEach { (grupo, moviesByGroup) ->
            item {
                Text(
                    text = grupo,
                    modifier = Modifier.padding(8.dp)
                )
            }
            item {
                LazyRow {
                    items(moviesByGroup.size) { index ->
                        MoviePoster(movie = moviesByGroup[index], navController = navController)
                        Spacer(modifier = Modifier.width(16.dp))
                    }
                }
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}


@Composable
fun MovieListItem(movie: Movie, navController: NavController) {
    val imageResourceId = getImageResourceId(movie.imageResource)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                navController.navigate("DetailScreen/${movie.title}")
            }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = imageResourceId),
            contentDescription = null,
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = movie.title)
            Text(text = movie.grupo)
            Text(text = movie.synopsis)
        }
    }
}

@Composable
fun DetailScreen(navController: NavController, movie: Movie) {
    val imageResourceId = getImageResourceId(movie.imageResource)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier
                    .clickable { navController.popBackStack() }
                    .size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = "Movie Details", fontWeight = FontWeight.Bold)
        }
        Image(
            painter = painterResource(id = imageResourceId),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
        Row() {
            Text(text = "Title: ", fontWeight = FontWeight.Bold)
            Text(text = "${movie.title}")
        }
        Row() {
            Text(text = "Group: ", fontWeight = FontWeight.Bold)
            Text(text = "${movie.grupo}")
        }
        Row() {
            Text(text = "Synopsis: ", fontWeight = FontWeight.Bold)
            Text(text = "${movie.synopsis}")
        }
        Row() {
            Text(text = "Original Title: ", fontWeight = FontWeight.Bold)
            Text(text = "${movie.originalTitle}")
        }
        Row() {
            Text(text = "Genre: ", fontWeight = FontWeight.Bold)
            Text(text = "${movie.genre}")
        }
        Row() {
            Text(text = "Episodes: ", fontWeight = FontWeight.Bold)
            Text(text = "${movie.episodes}")
        }
        Row() {
            Text(text = "Year: ", fontWeight = FontWeight.Bold)
            Text(text = "${movie.year}")
        }
        Row() {
            Text(text = "Country: ", fontWeight = FontWeight.Bold)
            Text(text = "${movie.country}")
        }
        Row() {
            Text(text = "Director: ", fontWeight = FontWeight.Bold)
            Text(text = "${movie.director}")
        }
        Row() {
            Text(text = "Cast: ", fontWeight = FontWeight.Bold)
            Text(text = "${movie.elenco}")
        }
        Row() {
            Text(text = "Available Until: ", fontWeight = FontWeight.Bold)
            Text(text = "${movie.availableUntil}")
        }
    }
}

object MovieContract {
    /* Inner class that defines the table contents */
    class MovieEntry : BaseColumns {
        companion object {
            const val TABLE_NAME = "movie"
            const val COLUMN_NAME_IMAGERESOURCE = "imageResource"
            const val COLUMN_NAME_TITLE = "title"
            const val COLUMN_NAME_GRUPO = "grupo"
            const val COLUMN_NAME_SYNOPSIS = "synopsis"
            const val COLUMN_NAME_ORIGINALTITLE = "originalTitle"
            const val COLUMN_NAME_GENRE = "genre"
            const val COLUMN_NAME_EPISODES = "episodes"
            const val COLUMN_NAME_YEAR = "year"
            const val COLUMN_NAME_COUNTRY = "country"
            const val COLUMN_NAME_DIRECTOR = "director"
            const val COLUMN_NAME_ELENCO = "elenco"
            const val COLUMN_NAME_AVAILABLEUNTIL = "availableUntil"
        }
    }
}

class MovieDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null,DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase?) {
        val SQL_CREATE_ENTRIES = "CREATE TABLE ${MovieContract.MovieEntry.TABLE_NAME} (" +
            "${BaseColumns._ID} INTEGER PRIMARY KEY," +
            "${MovieContract.MovieEntry.COLUMN_NAME_IMAGERESOURCE} TEXT," +
            "${MovieContract.MovieEntry.COLUMN_NAME_TITLE} TEXT," +
            "${MovieContract.MovieEntry.COLUMN_NAME_GRUPO} TEXT," +
            "${MovieContract.MovieEntry.COLUMN_NAME_SYNOPSIS} TEXT," +
            "${MovieContract.MovieEntry.COLUMN_NAME_ORIGINALTITLE} TEXT," +
            "${MovieContract.MovieEntry.COLUMN_NAME_GENRE} TEXT," +
            "${MovieContract.MovieEntry.COLUMN_NAME_EPISODES} TEXT," +
            "${MovieContract.MovieEntry.COLUMN_NAME_YEAR} TEXT," +
            "${MovieContract.MovieEntry.COLUMN_NAME_COUNTRY} TEXT," +
            "${MovieContract.MovieEntry.COLUMN_NAME_DIRECTOR} TEXT," +
            "${MovieContract.MovieEntry.COLUMN_NAME_ELENCO} TEXT," +
            "${MovieContract.MovieEntry.COLUMN_NAME_AVAILABLEUNTIL} TEXT" +
                ")"
        db?.execSQL(SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Aqui você pode adicionar lógica para atualizar o esquema do banco de dados se necessário
    }

    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "Movies.db"
    }

}

class MovieDataSource(context: Context) {
    private val dbHelper = MovieDbHelper(context)

    fun insertMovie(movie: Movie) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(MovieContract.MovieEntry.COLUMN_NAME_IMAGERESOURCE, movie.imageResource)
            put(MovieContract.MovieEntry.COLUMN_NAME_TITLE, movie.title)
            put(MovieContract.MovieEntry.COLUMN_NAME_GRUPO, movie.grupo)
            put(MovieContract.MovieEntry.COLUMN_NAME_SYNOPSIS, movie.synopsis)
            put(MovieContract.MovieEntry.COLUMN_NAME_ORIGINALTITLE, movie.originalTitle)
            put(MovieContract.MovieEntry.COLUMN_NAME_GENRE, movie.genre)
            put(MovieContract.MovieEntry.COLUMN_NAME_EPISODES, movie.episodes)
            put(MovieContract.MovieEntry.COLUMN_NAME_YEAR, movie.year)
            put(MovieContract.MovieEntry.COLUMN_NAME_COUNTRY, movie.country)
            put(MovieContract.MovieEntry.COLUMN_NAME_DIRECTOR, movie.director)
            put(MovieContract.MovieEntry.COLUMN_NAME_ELENCO, movie.elenco)
            put(MovieContract.MovieEntry.COLUMN_NAME_AVAILABLEUNTIL, movie.availableUntil)
            put(MovieContract.MovieEntry.COLUMN_NAME_IMAGERESOURCE, movie.imageResource)
            put(MovieContract.MovieEntry.COLUMN_NAME_IMAGERESOURCE, movie.imageResource)
        }
        val newRowId = db.insert(MovieContract.MovieEntry.TABLE_NAME,null,values)
    }

    fun getAllMovies(): List<Movie> {
        val db = dbHelper.readableDatabase
        val projection = arrayOf(
            BaseColumns._ID,
            MovieContract.MovieEntry.COLUMN_NAME_IMAGERESOURCE,
            MovieContract.MovieEntry.COLUMN_NAME_TITLE,
            MovieContract.MovieEntry.COLUMN_NAME_GRUPO,
            MovieContract.MovieEntry.COLUMN_NAME_SYNOPSIS,
            MovieContract.MovieEntry.COLUMN_NAME_ORIGINALTITLE,
            MovieContract.MovieEntry.COLUMN_NAME_GENRE,
            MovieContract.MovieEntry.COLUMN_NAME_EPISODES,
            MovieContract.MovieEntry.COLUMN_NAME_YEAR,
            MovieContract.MovieEntry.COLUMN_NAME_COUNTRY,
            MovieContract.MovieEntry.COLUMN_NAME_DIRECTOR,
            MovieContract.MovieEntry.COLUMN_NAME_ELENCO,
            MovieContract.MovieEntry.COLUMN_NAME_AVAILABLEUNTIL
        )
        val sortOrder = "${MovieContract.MovieEntry.COLUMN_NAME_TITLE} ASC"
        val cursor = db.query(
            MovieContract.MovieEntry.TABLE_NAME,
            projection,
            null,
            null,
            null,
            null,
            sortOrder
        )

        val movies = mutableListOf<Movie>()
        with(cursor) {
            while(moveToNext()) {
                val imageResource = getString(getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_NAME_IMAGERESOURCE))
                val title = getString(getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_NAME_TITLE))
                val grupo = getString(getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_NAME_GRUPO))
                val synopsis = getString(getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_NAME_SYNOPSIS))
                val originalTitle = getString(getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_NAME_ORIGINALTITLE))
                val genre = getString(getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_NAME_GENRE))
                val episodes = getInt(getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_NAME_EPISODES))
                val year = getInt(getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_NAME_YEAR))
                val country = getString(getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_NAME_COUNTRY))
                val director = getString(getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_NAME_DIRECTOR))
                val elenco = getString(getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_NAME_ELENCO))
                val availableUntil = getString(getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_NAME_AVAILABLEUNTIL))
                val movie = Movie(imageResource=imageResource,title=title,grupo=grupo,synopsis=synopsis,originalTitle=originalTitle,genre=genre,episodes=episodes,year=year,country=country,director=director,elenco=elenco,availableUntil=availableUntil)
                movies.add(movie)
            }
        }
        cursor.close()
        return movies
    }
    // Método para obter o caminho do banco de dados
    fun getDatabasePath(): String {
        return dbHelper.writableDatabase.path
    }

    fun populateMovies() {
        insertMovie(Movie("image1", "Title1", "Grupo1", "Synopsis1", "OriginalTitle1", "Genre1", 10, 2020, "Country1", "Director1", "Elenco1", "2025-12-31"))
        insertMovie(Movie("image2", "Title2", "Grupo1", "Synopsis2", "OriginalTitle2", "Genre2", 20, 2020, "Country2", "Director2", "Elenco2", "2025-12-31"))
        insertMovie(Movie("image3", "Title3", "Grupo1", "Synopsis3", "OriginalTitle3", "Genre3", 20, 2020, "Country3", "Director3", "Elenco3", "2025-12-31"))
        insertMovie(Movie("image4", "Title4", "Grupo1", "Synopsis4", "OriginalTitle4", "Genre4", 20, 2021, "Country4", "Director4", "Elenco4", "2025-12-31"))
        insertMovie(Movie("image5", "Title5", "Grupo1", "Synopsis5", "OriginalTitle5", "Genre5", 20, 2022, "Country5", "Director5", "Elenco5", "2025-12-31"))
        insertMovie(Movie("image6", "Title6", "Grupo2", "Synopsis6", "OriginalTitle6", "Genre6", 20, 2022, "Country6", "Director6", "Elenco6", "2025-12-31"))
        insertMovie(Movie("image7", "Title7", "Grupo2", "Synopsis7", "OriginalTitle7", "Genre7", 20, 2022, "Country7", "Director7", "Elenco7", "2025-12-31"))
        insertMovie(Movie("image8", "Title8", "Grupo2", "Synopsis8", "OriginalTitle8", "Genre8", 20, 2023, "Country8", "Director8", "Elenco8", "2025-12-31"))
        insertMovie(Movie("image9", "Title9", "Grupo2", "Synopsis9", "OriginalTitle9", "Genre9", 20, 2022, "Country9", "Director9", "Elenco9", "2025-12-31"))
        insertMovie(Movie("image10", "Title10", "Grupo2", "Synopsis10", "OriginalTitle10", "Genre10", 20, 2022, "Country10", "Director10", "Elenco10", "2025-12-31"))
        insertMovie(Movie("image11", "Title11", "Grupo3", "Synopsis11", "OriginalTitle11", "Genre11", 20, 2023, "Country11", "Director11", "Elenco11", "2025-12-31"))
        insertMovie(Movie("image12", "Title12", "Grupo3", "Synopsis12", "OriginalTitle12", "Genre12", 20, 2023, "Country12", "Director12", "Elenco12", "2025-12-31"))
        insertMovie(Movie("image13", "Title13", "Grupo3", "Synopsis13", "OriginalTitle13", "Genre13", 20, 2024, "Country13", "Director13", "Elenco13", "2025-12-31"))
        insertMovie(Movie("image14", "Title14", "Grupo3", "Synopsis14", "OriginalTitle14", "Genre14", 20, 2024, "Country14", "Director14", "Elenco14", "2025-12-31"))
        insertMovie(Movie("image15", "Title15", "Grupo3", "Synopsis15", "OriginalTitle15", "Genre15", 20, 2024, "Country15", "Director15", "Elenco15", "2025-12-31"))

    }

    // Função para excluir todos os filmes do banco de dados
    fun deleteAllMovies() {
        val db = dbHelper.writableDatabase
        db.delete(MovieContract.MovieEntry.TABLE_NAME, null, null)
    }


}




data class Movie(
    val imageResource: String,
    val title: String,
    val grupo: String,
    val synopsis: String,
    val originalTitle: String,
    val genre: String,
    val episodes: Int,
    val year: Int,
    val country: String,
    val director: String,
    val elenco: String,
    val availableUntil: String
)

@Composable
fun MoviePoster(movie: Movie, navController: NavController) {
    val imageResourceId = getImageResourceId(movie.imageResource)
    Column(
        modifier = Modifier.clickable {
            navController.navigate("DetailScreen/${movie.title}")
        }
    ) {
        Image(
            painter = painterResource(id = imageResourceId), // Substitua "placeholder" pelo nome da sua imagem padrão
            contentDescription = null,
            modifier = Modifier
                .size(120.dp, 180.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
        Text(
            text = movie.title,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

fun findMovieByTitle(title: String?, movies: List<Movie>): Movie? {
    // Verifica se o título não é nulo
    if (title.isNullOrEmpty()) {
        return null
    }

    // Busca o filme na lista de filmes
    return movies.find { it.title == title }
}

/*
fun readMoviesFromJson(assetManager: AssetManager): List<Movie> {
    val inputStream: InputStream = assetManager.open("movies.json")
    val json = inputStream.bufferedReader().use { it.readText() }
    return Gson().fromJson(json, object : TypeToken<List<Movie>>() {}.type)
}
*/

fun getImageResourceId(imageName: String): Int {
    // Obtenha a classe R.drawable
    val clazz = R.drawable::class.java
    try {
        // Obtenha o ID do campo da imagem pelo seu nome usando reflexão
        val field = clazz.getDeclaredField(imageName)
        // Como os campos são estáticos, passe null como o objeto de instância
        return field.getInt(null)
    } catch (e: Exception) {
        e.printStackTrace()
    }
    // Se não encontrar a imagem, retorne um valor padrão
    return -1
}