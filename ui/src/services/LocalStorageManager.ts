class LocalStorageManager {
    static save(key: string, data: any): void {
        try {
            const serializedData = JSON.stringify(data);
            localStorage.setItem(key, serializedData);
        } catch (error) {
            console.error(`Error saving to localStorage: ${error}`);
        }
    }

    static get<T>(key: string): any {
        try {
            const serializedData = localStorage.getItem(key);
            if (serializedData === null) {
                return undefined;
            }
            return JSON.parse(serializedData) as T;
        } catch (error) {
            console.error(`Error getting data from localStorage: ${error}`);
            return undefined;
        }
    }
}

export default LocalStorageManager;