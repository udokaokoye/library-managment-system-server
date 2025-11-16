import Image from "next/image";
import { sampleBooks } from "../../../lib/books";

export async function generateStaticParams() {
    return sampleBooks.map((b) => ({ id: b.id }));
}

export default function BookPage({ params }) {
    const { id } = params;
    const book = sampleBooks.find((b) => b.id === id);

    if (!book) {
        return <div className="container mx-auto p-6">Book not found</div>;
    }

    return (
        <div className="container mx-auto px-6 py-8">
            <div className="grid md:grid-cols-[280px_1fr] gap-8">
                <div className="flex justify-center md:justify-start">
                    <div className="relative w-full max-w-[240px] shadow-lg rounded-md overflow-hidden bg-gray-100">
                        <Image
                            src={book.cover}
                            alt={`${book.title} cover`}
                            width={240}
                            height={360}
                            className="w-full h-auto object-cover"
                            priority
                        />
                    </div>
                </div>
                <div>
                    <h1 className="text-3xl font-semibold mb-3">{book.title}</h1>
                    <div className="text-base text-[color:var(--muted)] mb-6">{book.author} — {book.year}</div>
                    <p className="mb-6 text-lg leading-relaxed">{book.description}</p>
                    <div className="flex gap-3">
                        <button className="btn-primary px-4 py-2 rounded">Borrow</button>
                        <button className="btn-outline px-4 py-2 rounded">Reserve</button>
                    </div>
                </div>
            </div>
        </div>
    );
}
