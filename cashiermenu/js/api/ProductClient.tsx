import 'whatwg-fetch'
import {components, operations} from "js/api/ProductClient";
import {DELETE, GET, PUT} from "js/api/CommonClient";

export namespace TopicClient {
    export type TopicData = components["schemas"]["TopicData"];

    export const getAllTopics = () => GET(
        "/topic/all",
        {})

    export const createTopic = (body: components["schemas"]["CreateTopicRequest"]) => PUT(
        "/topic/create",
        {
            body
        })

    export const deleteTopic = (query: operations["topicDelete"]["parameters"]["query"]) => DELETE(
        "/topic/delete",
        {
            params: {query}
        })

    export const getTopicChildren = (query: operations["topicChildren"]["parameters"]["query"]) => GET(
        "/topic/children",
        {
            params: {query}
        })
}