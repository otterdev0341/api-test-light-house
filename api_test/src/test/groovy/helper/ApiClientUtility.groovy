package helper
import groovy.json.JsonSlurper

/**
 * A simple API client to fetch and parse JSON data from a URL.
 */
class ApiClientUtility {

    /**
     * Fetches data from the given URL, parses it as JSON, and returns the resulting Groovy object.
     *
     * @param apiUrl The full URL to fetch data from (e.g., "https://jsonplaceholder.typicode.com/todos/1").
     * @return A Groovy object (Map or List) representing the parsed JSON, or null if an error occurs.
     */
    def fetchJsonFromUrl(String apiUrl) {
        if (apiUrl == null || apiUrl.trim().isEmpty()) {
            println "Error: API URL cannot be null or empty."
            return null
        }

        try {
            // 1. Create a URL object
            URL url = new URL(apiUrl)

            // 2. Fetch the content from the URL
            // .text performs an HTTP GET and returns the response body as a String
            String jsonResponse = url.text

            // 3. Parse the JSON string
            JsonSlurper slurper = new JsonSlurper()
            def parsedJson = slurper.parseText(jsonResponse)

            return parsedJson
        } catch (MalformedURLException e) {
            println "Error: Invalid API URL provided: ${apiUrl}. Details: ${e.getMessage()}"
            return null
        } catch (IOException e) {
            // Handles network issues, server errors (like 404, 500 if .text throws them)
            println "Error fetching data from URL ${apiUrl}: ${e.getMessage()}"
            return null
        } catch (Exception e) { // Catch other potential parsing errors or unexpected issues
            println "An unexpected error occurred while processing URL ${apiUrl}: ${e.getMessage()}"
            return null
        }
    }
}

// Example usage:
// You would typically put this in a script or another class (like a Spock test)

// def client = new ApiClient()
// def todoUrl = 'https://jsonplaceholder.typicode.com/todos/1'
// def todoData = client.fetchJsonFromUrl(todoUrl)

// if (todoData) {
//     println "Successfully fetched and parsed JSON:"
//     println todoData
//     println "User ID: ${todoData.userId}"
//     println "Title: ${todoData.title}"
// } else {
//     println "Failed to fetch or parse data from ${todoUrl}"
// }

// def postsUrl = 'https://jsonplaceholder.typicode.com/posts'
// def postsData = client.fetchJsonFromUrl(postsUrl)
// if (postsData && postsData instanceof List) {
//     println "\nSuccessfully fetched posts (list):"
//     println "Number of posts: ${postsData.size()}"
//     if (postsData.size() > 0) {
//         println "First post title: ${postsData[0].title}"
//     }
// } else {
//     println "Failed to fetch or parse data from ${postsUrl}"
// }

// Example with an invalid URL
// def invalidData = client.fetchJsonFromUrl("htp:/invalid-url")
// if (!invalidData) {
//    println "\nHandled invalid URL as expected."
// }