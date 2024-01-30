import interact from "interactjs";

export function useResizableInteract(clazz: string): void {
    interact(clazz).resizable({
        edges: { left: false, right: false, bottom: true, top: false },
        listeners: {
            move(event) {
                let { x, y } = event.target.dataset

                event.target.style.width = event.rect.width + 'px'
                event.target.style.height = event.rect.height + 'px'

                x += event.deltaRect.left
                y += event.deltaRect.top

                event.target.style.webkitTransform = event.target.style.transform = 'translate(' + x + 'px,' + y + 'px)'

                event.target.dataset.x = x
                event.target.dataset.y = y
            }
        },
        modifiers: [
            interact.modifiers.restrictEdges({
                outer: 'parent'
            }),

            interact.modifiers.restrictSize({
                min: { width: 100, height: 50 }
            })
        ],
        inertia: true
    })
}