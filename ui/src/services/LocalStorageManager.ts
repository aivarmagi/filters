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

    static remove(key: string): void {
        try {
            localStorage.removeItem(key);
        } catch (error) {
            console.error(`Error removing data from localStorage: ${error}`);
        }
    }

    static hasKey(key: string): boolean {
        try {
            return localStorage.getItem(key) !== null;
        } catch (error) {
            return false;
        }
    }
}

export default LocalStorageManager;